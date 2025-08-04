package com.example.drosckar.core.data.run

import com.example.drosckar.core.domain.run.LocalRunDataSource
import com.example.drosckar.core.domain.run.RemoteRunDataSource
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.domain.run.RunId
import com.example.drosckar.core.domain.run.RunRepository
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [RunRepository] that follows the offline-first pattern.
 *
 * This class:
 * - Treats the local database as the single source of truth.
 * - Syncs data between local and remote sources.
 * - Uses [applicationScope] to safely complete critical operations
 *   even if the current coroutine scope (e.g., ViewModel) is cancelled.
 */
class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        // Always listen to the local DB as the single source of truth.
        return localRunDataSource.getRuns()
    }

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
                // ❗ Not yet syncing failed posts — simply return success for now.
                Result.Success(Unit)
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

    override suspend fun deleteRun(id: RunId) {
        // Immediately delete the run from local DB.
        localRunDataSource.deleteRun(id)

        // ⚠️ Ensure remote deletion happens even if ViewModel gets destroyed.
        applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }
}