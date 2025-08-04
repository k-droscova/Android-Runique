package com.example.drosckar.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.drosckar.core.database.dao.RunPendingSyncDao
import com.example.drosckar.core.database.mappers.toRun
import com.example.drosckar.core.domain.run.RemoteRunDataSource

/**
 * Worker responsible for syncing a locally saved run (with map image)
 * to the remote server if it previously failed (e.g., due to no internet).
 *
 * This worker:
 * - Retrieves a pending run entity by ID from the Room DB.
 * - Sends it to the API.
 * - On success, removes it from the pending sync table.
 *
 * Triggered when upserting a run fails remotely but succeeds locally.
 */
class CreateRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Prevent retrying forever. Abort after 5 failed attempts.
        if(runAttemptCount >= 5) {
            return Result.failure()
        }

        // Extract the run ID passed as input to this worker.
        val pendingRunId = params.inputData.getString(RUN_ID) ?: return Result.failure()

        // Fetch the pending run entity from Room. Abort if not found.
        val pendingRunEntity = pendingSyncDao.getRunPendingSyncEntity(pendingRunId)
            ?: return Result.failure()

        // Convert the embedded RunEntity to the domain model.
        val run = pendingRunEntity.run.toRun()

        // Try syncing the run with its map image to the server.
        return when(val result = remoteRunDataSource.postRun(run, pendingRunEntity.mapPictureBytes)) {
            is com.example.drosckar.core.domain.util.Result.Error -> {
                // Map domain error to Worker retry/failure signal.
                result.error.toWorkerResult()
            }

            is com.example.drosckar.core.domain.util.Result.Success -> {
                // On success, delete from pending sync table so we don’t retry again.
                pendingSyncDao.deleteRunPendingSyncEntity(pendingRunId)
                Result.success()
            }
        }
    }

    companion object {
        /** InputData key for passing the run ID to this worker. */
        const val RUN_ID = "RUN_ID"
    }
}