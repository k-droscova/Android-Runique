package com.example.drosckar.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.drosckar.core.database.dao.RunDao
import com.example.drosckar.core.database.dao.RunPendingSyncDao
import com.example.drosckar.core.database.entity.DeletedRunSyncEntity
import com.example.drosckar.core.database.entity.RunEntity
import com.example.drosckar.core.database.entity.RunPendingSyncEntity

/**
 * The main Room database for storing runs.
 *
 * Manages versioning and provides access to DAO interfaces.
 */
@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 2,
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
}