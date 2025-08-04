package com.example.drosckar.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.drosckar.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the `runentity` table.
 *
 * Defines SQL queries for interacting with the local run database.
 */
@Dao
interface RunDao {

    /**
     * Inserts or updates a single run.
     * If the run with the same ID exists, it will be updated.
     */
    @Upsert
    suspend fun upsertRun(run: RunEntity)

    /**
     * Inserts or updates a list of runs in a single transaction.
     */
    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)

    /**
     * Retrieves all runs sorted by timestamp descending.
     * Returns a reactive stream to support UI updates.
     */
    @Query("SELECT * FROM runentity ORDER BY dateTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    /**
     * Deletes a single run by its ID.
     */
    @Query("DELETE FROM runentity WHERE id=:id")
    suspend fun deleteRun(id: String)

    /**
     * Deletes all runs from the database.
     */
    @Query("DELETE FROM runentity")
    suspend fun deleteAllRuns()
}