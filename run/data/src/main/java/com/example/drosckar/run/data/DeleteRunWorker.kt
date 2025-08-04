package com.example.drosckar.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.drosckar.core.database.dao.RunPendingSyncDao
import com.example.drosckar.core.domain.run.RemoteRunDataSource

/**
 * Worker responsible for retrying deletion of a run from the remote server
 * if it previously failed (e.g., offline scenario).
 *
 * Triggered when a local run is deleted, but the remote API call fails.
 */
class DeleteRunWorker(
    context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5) {
            return Result.failure()
        }

        // Retrieve the ID of the run that needs deletion.
        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()

        // Attempt to delete the run from the remote API.
        return when(val result = remoteRunDataSource.deleteRun(runId)) {
            is com.example.drosckar.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }

            is com.example.drosckar.core.domain.util.Result.Success -> {
                // On success, remove it from the deleted runs sync table.
                pendingSyncDao.deleteDeletedRunSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        /** InputData key for passing the run ID to this worker. */
        const val RUN_ID = "RUN_ID"
    }
}