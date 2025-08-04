package com.example.drosckar.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.drosckar.core.database.entity.DeletedRunSyncEntity
import com.example.drosckar.core.database.entity.RunPendingSyncEntity

/**
 * Data Access Object for managing run sync operations in offline-first mode.
 *
 * Handles both locally created and locally deleted runs that need to be synced
 * with the remote API at a later point in time.
 */
@Dao
interface RunPendingSyncDao {

    // -------------------- Created Runs --------------------

    /**
     * Returns all runs created locally by the given user but not yet synced.
     */
    @Query("SELECT * FROM runpendingsyncentity WHERE userId=:userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    /**
     * Returns a specific pending run sync entity by run ID.
     */
    @Query("SELECT * FROM runpendingsyncentity WHERE runId=:runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    /**
     * Inserts or updates a run pending sync entity.
     * Called when a run is created but fails to sync to the backend.
     */
    @Upsert
    suspend fun upsertRunPendingSyncEntity(entity: RunPendingSyncEntity)

    /**
     * Deletes a pending run sync entity after successful remote sync
     * or if the run was deleted locally before syncing.
     */
    @Query("DELETE FROM runpendingsyncentity WHERE runId=:runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    // -------------------- Deleted Runs --------------------

    /**
     * Returns all runs deleted locally by the given user but not yet synced.
     */
    @Query("SELECT * FROM deletedrunsyncentity WHERE userId=:userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

    /**
     * Inserts or updates a deleted run sync entity.
     * Called when a local deletion fails to sync to the backend.
     */
    @Upsert
    suspend fun upsertDeletedRunSyncEntity(entity: DeletedRunSyncEntity)

    /**
     * Deletes a deleted run sync entity after successful remote deletion.
     */
    @Query("DELETE FROM deletedrunsyncentity WHERE runId=:runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}