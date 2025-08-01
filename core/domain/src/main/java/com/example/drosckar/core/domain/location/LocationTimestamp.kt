package com.example.drosckar.core.domain.location

import kotlin.time.Duration

/**
 * Represents a location fix recorded at a specific moment during an activity.
 *
 * Combines geographic position, altitude, and the elapsed duration from the start of the session.
 *
 * @property location The full location data including altitude.
 * @property durationTimestamp The time elapsed since the beginning of the run (e.g., 2 minutes in).
 */
data class LocationTimestamp(
    val location: LocationWithAltitude,
    val durationTimestamp: Duration
)