package com.example.drosckar.core.database.dao

import androidx.room.Dao
import androidx.room.Query

/**
 * DAO (Data Access Object) for querying analytics-related data
 * from the RunEntity table in the Room database.
 *
 * Provides aggregate queries for calculating running statistics such as:
 * - Total distance
 * - Total duration
 * - Maximum speed
 * - Average distance per run
 * - Average pace per run
 *
 * These values will be used to populate the Analytics dashboard.
 */
@Dao
interface AnalyticsDao {

    /**
     * Returns the total distance run across all entries in meters.
     */
    @Query("SELECT SUM(distanceMeters) FROM runentity")
    suspend fun getTotalDistance(): Int

    /**
     * Returns the total time run in milliseconds.
     */
    @Query("SELECT SUM(durationMillis) FROM runentity")
    suspend fun getTotalTimeRun(): Long

    /**
     * Returns the highest maximum speed (km/h) achieved in a single run.
     */
    @Query("SELECT MAX(maxSpeedKmh) FROM runentity")
    suspend fun getMaxRunSpeed(): Double

    /**
     * Returns the average distance per run in meters.
     */
    @Query("SELECT AVG(distanceMeters) FROM runentity")
    suspend fun getAvgDistancePerRun(): Double

    /**
     * Returns the average pace (min/km) across all runs.
     *
     * The formula used:
     *  (durationMillis / 60_000.0) → converts duration to minutes
     *  (distanceMeters / 1_000.0) → converts distance to kilometers
     *  → minutes per kilometer = min / km
     */
    @Query("SELECT AVG((durationMillis / 60000.0) / (distanceMeters / 1000.0)) FROM runentity")
    suspend fun getAvgPacePerRun(): Double
}