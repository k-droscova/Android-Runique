package com.example.drosckar.run.presentation.run_overview.model

/**
 * UI model used to display run data in a user-friendly, formatted way.
 *
 * This model contains only formatted strings and values directly usable by the UI layer.
 *
 * @property id Unique identifier of the run.
 * @property duration Formatted duration string (e.g., "00:45:23").
 * @property dateTime Formatted local date/time string (e.g., "Aug 01, 2025 - 07:30PM").
 * @property distance Formatted distance (e.g., "5.2 km").
 * @property avgSpeed Formatted average speed (e.g., "10.3 km/h").
 * @property maxSpeed Formatted maximum speed (e.g., "15.0 km/h").
 * @property pace Formatted pace (e.g., "05:30 min/km").
 * @property totalElevation Formatted elevation gain (e.g., "100 m").
 * @property mapPictureUrl Optional URL to a static map image.
 */
data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?
)