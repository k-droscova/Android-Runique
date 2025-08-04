package com.example.drosckar.run.network.util

import kotlinx.serialization.Serializable

/**
 * Serializable request body for creating a new run via the backend API.
 *
 * Unlike [RunDto], this only includes fields the client provides (no image URL).
 */
@Serializable
data class CreateRunRequest(
    val durationMillis: Long,
    val distanceMeters: Int,
    val epochMillis: Long, // time of run in UTC milliseconds
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val id: String
)