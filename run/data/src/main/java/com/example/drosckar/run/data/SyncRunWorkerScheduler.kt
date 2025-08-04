package com.example.drosckar.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.example.drosckar.core.database.dao.RunPendingSyncDao
import com.example.drosckar.core.database.entity.DeletedRunSyncEntity
import com.example.drosckar.core.database.entity.RunPendingSyncEntity
import com.example.drosckar.core.database.mappers.toRunEntity
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.domain.run.RunId
import com.example.drosckar.core.domain.run.SyncRunScheduler
import com.example.drosckar.core.domain.util.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Concrete implementation of [SyncRunScheduler] using WorkManager.
 *
 * Handles background scheduling for:
 * - Fetching runs periodically
 * - Creating a run remotely when it couldn't be sent immediately
 * - Deleting a run remotely if offline
 *
 * Uses constraints and exponential backoff to retry on failures.
 */
class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        when (type) {
            is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(type.interval)
            is SyncRunScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)
            is SyncRunScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(
                run = type.run,
                mapPictureBytes = type.mapPictureBytes
            )
        }
    }

    /**
     * Schedules a one-time background sync to delete a run remotely.
     */
    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = sessionStorage.get()?.userId ?: return

        val entity = DeletedRunSyncEntity(runId = runId, userId = userId)
        pendingSyncDao.upsertDeletedRunSyncEntity(entity)

        val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag("delete_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                2000L,
                TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, entity.runId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    /**
     * Schedules a one-time background sync to create a run remotely.
     */
    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return

        val pendingRun = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        pendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag("create_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                2000L,
                TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    /**
     * Schedules a periodic sync to fetch runs in the background.
     *
     * Ensures only one such periodic worker is active at any time.
     */
    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }

        // Don't schedule duplicate periodic workers.
        if (isSyncScheduled) return

        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
            repeatInterval = interval.toJavaDuration()
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                2000L,
                TimeUnit.MILLISECONDS
            )
            .setInitialDelay(30, TimeUnit.MINUTES) // Delay because we already fetch on init
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }

    /**
     * Cancels all scheduled syncs (e.g., on logout).
     */
    override suspend fun cancelAllSyncs() {
        workManager.cancelAllWork().await()
    }
}