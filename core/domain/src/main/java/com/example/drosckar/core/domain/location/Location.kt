package com.example.drosckar.core.domain.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a geographic coordinate using latitude and longitude.
 *
 * @property lat Latitude in decimal degrees. Positive for north, negative for south.
 * @property long Longitude in decimal degrees. Positive for east, negative for west.
 */
data class Location(
    val lat: Double,
    val long: Double
) {
    /**
     * Calculates the distance in meters to another [Location] using the Haversine formula.
     * Takes Earth curvature into account.
     */
    fun distanceTo(other: Location): Float {
        val latDistance = Math.toRadians(other.lat - lat)
        val longDistance = Math.toRadians(other.long - long)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(other.lat)) *
                sin(longDistance / 2) * sin(longDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c.toFloat()
    }

    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000
    }
}