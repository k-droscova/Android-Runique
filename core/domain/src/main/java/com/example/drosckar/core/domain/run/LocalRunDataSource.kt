package com.example.drosckar.core.domain.run

import kotlinx.coroutines.flow.Flow
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.Result


/**
 * Type alias to improve readability of ID usage throughout the codebase.
 */
typealias RunId = String

/**
 * Interface that abstracts local data access to runs.
 *
 * Defines domain-level access methods, independent of database implementation (Room).
 */
interface LocalRunDataSource {

    /**
     * Returns a stream of domain `Run` models, sorted by newest first.
     */
    fun getRuns(): Flow<List<Run>>

    /**
     * Upserts a single run and returns either its ID or a local DB error.
     */
    suspend fun upsertRun(run: Run): Result<RunId, DataError.Local>

    /**
     * Upserts multiple runs and returns their IDs or a local DB error.
     */
    suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local>

    /**
     * Deletes a single run by its ID.
     */
    suspend fun deleteRun(id: RunId)

    /**
     * Deletes all runs in the database.
     */
    suspend fun deleteAllRuns()
}