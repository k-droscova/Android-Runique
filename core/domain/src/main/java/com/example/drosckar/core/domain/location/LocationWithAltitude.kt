package com.example.drosckar.core.domain.location

/**
 * Represents a geographic location with altitude information.
 *
 * Useful for tracking elevation changes during a run or hike.
 *
 * @property location The geographic coordinates (latitude and longitude).
 * @property altitude The altitude in meters above sea level.
 */
data class LocationWithAltitude(
    val location: Location,
    val altitude: Double
)