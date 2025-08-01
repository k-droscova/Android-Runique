package com.example.drosckar.run.domain

import com.example.drosckar.core.domain.location.LocationTimestamp
import kotlin.time.Duration

/**
 * Represents the core data of a completed or ongoing run.
 *
 * @property distanceMeters The total distance of the run in meters.
 * @property pace The average pace per kilometer (e.g., 5:00/km), calculated over the total distance.
 * @property locations A list of location segments. Each inner list represents a continuous part of the route,
 *                     i.e., from start until pause, or from resume until next pause.
 *                     This allows preserving gaps when the user pauses the run.
 */
data class RunData(
    val distanceMeters: Int = 0,

    // Average pace across the entire run, calculated as time / distance.
    val pace: Duration = Duration.ZERO,

    // List of segments: each sublist represents a portion of the route between pause/resume actions.
    val locations: List<List<LocationTimestamp>> = emptyList()
)