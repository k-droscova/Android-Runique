package com.example.drosckar.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Create table for RunPendingSyncEntity
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS RunPendingSyncEntity (
                durationMillis INTEGER NOT NULL,
                distanceMeters INTEGER NOT NULL,
                dateTimeUtc TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                avgSpeedKmh REAL NOT NULL,
                maxSpeedKmh REAL NOT NULL,
                totalElevationMeters INTEGER NOT NULL,
                mapPictureUrl TEXT,
                id TEXT NOT NULL,
                runId TEXT NOT NULL PRIMARY KEY,
                mapPictureBytes BLOB NOT NULL,
                userId TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Create table for DeletedRunSyncEntity
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS DeletedRunSyncEntity (
                runId TEXT NOT NULL PRIMARY KEY,
                userId TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}