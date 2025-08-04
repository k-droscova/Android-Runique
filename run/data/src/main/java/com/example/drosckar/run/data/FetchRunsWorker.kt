package com.example.drosckar.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.drosckar.core.domain.run.RunRepository

/**
 * Worker that periodically fetches runs from the server
 * and inserts them into the local DB to keep it in sync.
 *
 * Useful for:
 * - Multi-device syncing
 * - Background updates (e.g., every 15 min)
 */
class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Stop retrying after 5 failed attempts.
        if(runAttemptCount >= 5) {
            return Result.failure()
        }

        // Attempt to fetch and locally store latest runs.
        return when(val result = runRepository.fetchRuns()) {
            is com.example.drosckar.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }

            is com.example.drosckar.core.domain.util.Result.Success -> Result.success()
        }
    }
}