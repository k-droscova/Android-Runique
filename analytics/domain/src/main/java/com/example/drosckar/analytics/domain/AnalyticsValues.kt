package com.example.drosckar.analytics.domain

import kotlin.time.Duration

/**
 * Domain model representing aggregated running analytics.
 *
 * These values are computed in the data layer and passed to the presentation
 * layer for display. This model serves as a neutral format between data and UI.
 *
 * @property totalDistanceRun Sum of all distances in meters.
 * @property totalTimeRun Total time run as a Kotlin Duration.
 * @property fastestEverRun Maximum recorded speed (km/h).
 * @property avgDistancePerRun Average distance run per session in meters.
 * @property avgPacePerRun Average pace in min/km.
 */
data class AnalyticsValues(
    val totalDistanceRun: Int = 0,
    val totalTimeRun: Duration = Duration.ZERO,
    val fastestEverRun: Double = 0.0,
    val avgDistancePerRun: Double = 0.0,
    val avgPacePerRun: Double = 0.0
)