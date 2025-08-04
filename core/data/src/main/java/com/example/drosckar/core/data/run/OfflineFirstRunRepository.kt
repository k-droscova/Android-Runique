package com.example.drosckar.core.data.run

import com.example.drosckar.core.database.dao.RunPendingSyncDao
import com.example.drosckar.core.database.mappers.toRun
import com.example.drosckar.core.domain.run.LocalRunDataSource
import com.example.drosckar.core.domain.run.RemoteRunDataSource
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.domain.run.RunId
import com.example.drosckar.core.domain.run.RunRepository
import com.example.drosckar.core.domain.run.SyncRunScheduler
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.domain.util.SessionStorage
import com.example.drosckar.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of [RunRepository] that follows the offline-first pattern.
 *
 * Responsibilities:
 * - Uses the local Room database as the single source of truth.
 * - Fetches and pushes data from/to the remote API.
 * - Automatically retries failed network operations using [WorkManager]-backed scheduling.
 * - Uses [applicationScope] to avoid premature cancellation of critical background tasks.
 * - Maintains persistent sync state via [RunPendingSyncDao] to ensure resilience across app restarts.
 */
class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
) : RunRepository {

    /**
     * Returns a reactive stream of all runs stored locally.
     * This stream reflects real-time updates and serves as the UI’s source of truth.
     */
    override fun getRuns(): Flow<List<Run>> {
        // Always listen to the local DB as the single source of truth.
        return localRunDataSource.getRuns()
    }

    /**
     * Fetches runs from the remote server and stores them locally.
     * Uses [applicationScope] to ensure DB writes are not cancelled mid-process.
     */
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                // ⚠️ Use applicationScope here to guarantee DB write is not cancelled.
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    /**
     * Inserts or updates a run in the local DB and attempts to sync it with the server.
     * - If local insertion fails (e.g., DB is full), the operation is aborted.
     * - If remote upload fails (e.g., no internet), the run is scheduled for sync using [WorkManager].
     */
    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)

        if (localResult !is Result.Success) {
            // If local insertion failed (e.g., DB full), bail out.
            return localResult.asEmptyDataResult()
        }

        // Replace the placeholder ID with the real DB-generated ID.
        val runWithId = run.copy(id = localResult.data)

        // Push the run and image to the remote server.
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                // Schedule this run for background sync later via WorkManager
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId,
                            mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit) // Pretend success; the sync will happen later
            }
            is Result.Success -> {
                // Insert the fully processed run (with URL) into local DB.
                // ✅ Again, ensure it's not skipped due to coroutine cancellation.
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    /**
     * Deletes a run locally and attempts to delete it from the remote server.
     * - If the run was never synced, we just clean up the local pending entity.
     * - If remote deletion fails, the run is scheduled for deletion via WorkManager.
     */
    override suspend fun deleteRun(id: RunId) {
        // Immediately delete the run from local DB.
        localRunDataSource.deleteRun(id)

        // If the run was created offline and never synced, just remove it entirely.
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if(isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        // ⚠️ Ensure remote deletion happens even if ViewModel gets destroyed.
        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        // Schedule a delete sync if it failed
        if(remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(id)
                )
            }.join()
        }
    }

    /**
     * Synchronizes unsynced local changes (new or deleted runs) with the remote API.
     *
     * - Created runs are sent to the API with their image bytes.
     * - Deleted runs are removed from the API using only their run IDs.
     * - Successful syncs are then removed from the pending sync tables.
     * - Fails silently for failed operations — they’ll retry next time.
     */
    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            // Fetch all pending created and deleted runs for this user.
            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            // Launch a job for each pending *created* run to sync it with the backend.
            val createJobs = createdRuns.await().map { entity ->
                launch {
                    val run = entity.run.toRun()
                    when (remoteRunDataSource.postRun(run, entity.mapPictureBytes)) {
                        is Result.Error -> Unit
                        is Result.Success -> {
                            // Remove the pending sync entry if it was successfully synced.
                            applicationScope.launch {
                                runPendingSyncDao.deleteRunPendingSyncEntity(entity.runId)
                            }.join()
                        }
                    }
                }
            }

            // Launch a job for each pending *deleted* run to tell the backend to delete it.
            val deleteJobs = deletedRuns.await().map { entity ->
                launch {
                    when (remoteRunDataSource.deleteRun(entity.runId)) {
                        is Result.Error -> Unit
                        is Result.Success -> {
                            // Remove the deletion sync entry after successful deletion.
                            applicationScope.launch {
                                runPendingSyncDao.deleteDeletedRunSyncEntity(entity.runId)
                            }.join()
                        }
                    }
                }
            }

            // Wait for all sync jobs to finish before continuing.
            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }
}