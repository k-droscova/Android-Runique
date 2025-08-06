package com.example.drosckar.analytics.presentation.mappers

import com.example.drosckar.analytics.domain.AnalyticsValues
import com.example.drosckar.core.presentation.ui.formatted
import com.example.drosckar.core.presentation.ui.toFormattedKm
import com.example.drosckar.core.presentation.ui.toFormattedKmh
import com.example.drosckar.analytics.presentation.AnalyticsDashboardState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * Converts a [Duration] to a string in the format:
 * "Xd Yh Zm" (e.g. "1d 5h 23m")
 *
 * Days, hours, and minutes are calculated using the appropriate time units.
 */
fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return "${days}d ${hours}h ${minutes}m"
}

/**
 * Maps a domain [AnalyticsValues] model to a presentation [AnalyticsDashboardState].
 *
 * Also handles unit conversions and formatting for display in the UI.
 */
fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(), // meters → km
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),              // Duration → "Xd Yh Zm"
        fastestEverRun = fastestEverRun.toFormattedKmh(),                // Double → "12.5 km/h"
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKm(),      // meters → km
        avgPace = avgPacePerRun.seconds.formatted()                      // minutes/km → "5:42 /km"
    )
}