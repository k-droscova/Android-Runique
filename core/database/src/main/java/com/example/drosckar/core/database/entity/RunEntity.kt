package com.example.drosckar.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

/**
 * Represents a single run stored in the local Room database.
 *
 * This is the database model that maps to the `runentity` table.
 * It stores primitive fields only (e.g., Long, Int, String) that Room can persist.
 *
 * We use a MongoDB-compatible `ObjectId` as the primary key to:
 * - Ensure uniqueness of the ID both locally and remotely.
 * - Seamlessly sync the local DB with a MongoDB backend using the same identifier format.
 * - Allow offline-first behavior: generate unique IDs locally even before syncing.
 *
 * @property durationMillis Duration of the run in milliseconds.
 * @property distanceMeters Distance covered during the run in meters.
 * @property dateTimeUtc UTC timestamp as a string, normalized for time zone conversion.
 * @property latitude Latitude coordinate of the run start.
 * @property longitude Longitude coordinate of the run start.
 * @property avgSpeedKmh Average speed in km/h.
 * @property maxSpeedKmh Maximum speed in km/h.
 * @property totalElevationMeters Total elevation gain during the run in meters.
 * @property mapPictureUrl URL to a cached map image for the run (nullable).
 * @property id Unique identifier for the run. Matches backend ObjectId format.
 */
@Entity
data class RunEntity(
    val durationMillis: Long,
    val distanceMeters: Int,
    val dateTimeUtc: String,
    val latitude: Double,
    val longitude: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?,
    @PrimaryKey(autoGenerate = false)
    val id: String = ObjectId().toHexString() // Generates MongoDB-compatible ID
)