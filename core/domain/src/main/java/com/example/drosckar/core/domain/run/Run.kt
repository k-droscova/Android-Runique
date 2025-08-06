package com.example.drosckar.core.domain.run

import com.example.drosckar.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Domain model representing a single run.
 *
 * This model encapsulates raw run data which can be used for storage, calculations,
 * and syncing with the backend. For UI purposes, it should be converted to a [RunUi] model.
 *
 * @property id Unique identifier of the run. Null when the run is new and unsynced.
 * @property duration Total duration of the run.
 * @property dateTimeUtc Time the run started, in UTC timezone.
 * @property distanceMeters Distance covered during the run, in meters.
 * @property location Starting location of the run (can be used for resolving place names).
 * @property maxSpeedKmh Maximum speed achieved during the run, in km/h.
 * @property totalElevationMeters Total elevation gain in meters.
 * @property mapPictureUrl Optional URL to a static map image representing the run.
 */
data class Run(
    val id: String?, // null if this is a new run not yet synced
    val duration: Duration,
    val dateTimeUtc: ZonedDateTime,
    val distanceMeters: Int,
    val location: Location,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
) {
    /**
     * Average speed in km/h calculated from distance and duration.
     */
    val avgSpeedKmh: Double
        get() = (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
}