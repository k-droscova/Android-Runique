package com.example.drosckar.core.domain.run

import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

/**
 * Interface for a repository that manages the logic of run data access.
 * Provides a clean API for reading and writing runs, abstracting away
 * the local and remote data sources.
 */
interface RunRepository {

    /**
     * Returns a reactive stream of all runs stored locally.
     * This stream will emit updates when the local database changes.
     */
    fun getRuns(): Flow<List<Run>>

    /**
     * Fetches runs from the remote API and stores them locally.
     * Ensures local DB is the single source of truth.
     */
    suspend fun fetchRuns(): EmptyResult<DataError>

    /**
     * Inserts or updates a run both locally and remotely (including map image).
     */
    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError>

    /**
     * Deletes a run both locally and remotely.
     */
    suspend fun deleteRun(id: RunId)

    /**
     * Attempts to synchronize any locally stored, unsynced run operations
     * (creations or deletions) with the remote API.
     *
     * - Syncs runs created locally while offline.
     * - Syncs deletion requests that failed while offline.
     * - Ensures these changes are reflected on the backend before fetching fresh data.
     *//**
     * Attempts to synchronize any locally stored, unsynced run operations
     * (creations or deletions) with the remote API.
     *
     * - Syncs runs created locally while offline.
     * - Syncs deletion requests that failed while offline.
     * - Ensures these changes are reflected on the backend before fetching fresh data.
     */
    suspend fun syncPendingRuns()
}