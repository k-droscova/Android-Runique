package com.example.drosckar.run.network.dto

import kotlinx.serialization.Serializable

/**
 * Serializable Data Transfer Object (DTO) representing a run as returned by the backend API.
 *
 * Used for JSON serialization and deserialization. Fields must match the backend response exactly.
 */
@Serializable
data class RunDto(
    val id: String,
    val dateTimeUtc: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val lat: Double,
    val long: Double,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String?
)