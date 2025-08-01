package com.example.drosckar.core.domain.location

/**
 * Represents a geographic coordinate using latitude and longitude.
 *
 * @property lat Latitude in decimal degrees. Positive for north, negative for south.
 * @property long Longitude in decimal degrees. Positive for east, negative for west.
 */
data class Location(
    val lat: Double,
    val long: Double
)