package com.example.drosckar.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.drosckar.core.database.dao.RunDao
import com.example.drosckar.core.database.entity.RunEntity

/**
 * The main Room database for storing runs.
 *
 * Manages versioning and provides access to DAO interfaces.
 */
@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {
    abstract val runDao: RunDao
}