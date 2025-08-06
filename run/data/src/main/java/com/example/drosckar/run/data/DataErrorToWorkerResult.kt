package com.example.drosckar.run.data

import androidx.work.ListenableWorker
import com.example.drosckar.core.domain.util.DataError

/**
 * Maps domain-level [DataError]s to [ListenableWorker.Result] for WorkManager.
 * Determines whether a job should be retried or permanently failed.
 */
fun DataError.toWorkerResult(): ListenableWorker.Result {
    return when (this) {
        // Permanent local issues — no point in retrying until fixed by user.
        DataError.Local.DISK_FULL -> ListenableWorker.Result.failure()

        // Retry-able network errors — can be transient.
        DataError.Network.NO_INTERNET,
        DataError.Network.SERVER_ERROR,
        DataError.Network.REQUEST_TIMEOUT,
        DataError.Network.TOO_MANY_REQUESTS,
        DataError.Network.UNAUTHORIZED,
        DataError.Network.CONFLICT -> ListenableWorker.Result.retry()

        // Irrecoverable or developer/client-side bugs.
        DataError.Network.PAYLOAD_TOO_LARGE,
        DataError.Network.SERIALIZATION,
        DataError.Network.UNKNOWN -> ListenableWorker.Result.failure()
    }
}