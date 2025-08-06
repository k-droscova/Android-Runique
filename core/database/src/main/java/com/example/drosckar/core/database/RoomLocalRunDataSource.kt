package com.example.drosckar.core.database

import android.database.sqlite.SQLiteFullException
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.database.dao.RunDao
import com.example.drosckar.core.database.mappers.toRun
import com.example.drosckar.core.database.mappers.toRunEntity
import com.example.drosckar.core.domain.run.LocalRunDataSource
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.domain.run.RunId
import com.example.drosckar.core.domain.util.DataError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete Room-based implementation of `LocalRunDataSource`.
 *
 * Encapsulates Room DB interactions while providing domain-level models to the app.
 *
 * @param runDao The DAO interface to interact with the Room database.
 */
class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {

    /**
     * Retrieves all runs as domain models in real-time via Flow.
     */
    override fun getRuns(): Flow<List<Run>> {
        return runDao.getRuns()
            .map { runEntities ->
                runEntities.map { it.toRun() }
            }
    }

    /**
     * Inserts or updates a run and returns the inserted ID.
     * Handles `SQLiteFullException` to signal disk space issues.
     */
    override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> {
        return try {
            val entity = run.toRunEntity()
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    /**
     * Inserts or updates multiple runs and returns their IDs.
     * Handles `SQLiteFullException` to signal disk space issues.
     */
    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> {
        return try {
            val entities = runs.map { it.toRunEntity() }
            runDao.upsertRuns(entities)
            Result.Success(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    /**
     * Deletes a run by its unique identifier.
     */
    override suspend fun deleteRun(id: String) {
        runDao.deleteRun(id)
    }

    /**
     * Deletes all runs from the local database.
     */
    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }
}