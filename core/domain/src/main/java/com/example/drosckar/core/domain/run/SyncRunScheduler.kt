package com.example.drosckar.core.domain.run

import kotlin.time.Duration

/**
 * Interface for scheduling background sync operations related to runs.
 *
 * This abstraction allows scheduling different types of sync tasks—such as fetching,
 * creating, or deleting runs—using WorkManager or other implementations.
 */
interface SyncRunScheduler {

    /**
     * Schedules a sync operation of the given [type].
     *
     * @param type The specific type of sync task to schedule.
     */
    suspend fun scheduleSync(type: SyncType)

    /**
     * Cancels all currently scheduled sync tasks.
     *
     * Used on logout or app shutdown to prevent further syncing.
     */
    suspend fun cancelAllSyncs()

    /**
     * Represents a specific type of sync operation to be scheduled.
     */
    sealed interface SyncType {
        /**
         * Periodic background sync to fetch the latest runs from the server.
         * @param interval How often the fetch should occur.
         */
        data class FetchRuns(val interval: Duration): SyncType

        /**
         * One-time sync to delete a specific run from the remote server.
         * @param runId ID of the run to delete.
         */
        data class DeleteRun(val runId: RunId): SyncType

        /**
         * One-time sync to create a run remotely with associated map image.
         * @param run The run data to sync.
         * @param mapPictureBytes Byte array of the map image associated with the run.
         */
        class CreateRun(val run: Run, val mapPictureBytes: ByteArray): SyncType
    }
}