package com.example.drosckar.analytics.presentation

/**
 * UI state model for the Analytics Dashboard screen.
 *
 * Each property represents a formatted metric displayed in a dashboard card.
 * All values are pre-formatted as strings to simplify rendering in the Composable UI layer.
 *
 * This model could later be extended with more metrics or graph-related data.
 *
 * @property totalDistanceRun Total distance run across all sessions, as a formatted string (e.g., "12.4 km").
 * @property totalTimeRun Total time spent running across all sessions, formatted (e.g., "3h 24m").
 * @property fastestEverRun Highest average speed ever achieved in a run, formatted (e.g., "17.2 km/h").
 * @property avgDistance Average distance per run, formatted (e.g., "4.6 km").
 * @property avgPace Average pace per run (time per km), formatted (e.g., "5:42 /km").
 */
data class AnalyticsDashboardState(
    val totalDistanceRun: String,
    val totalTimeRun: String,
    val fastestEverRun: String,
    val avgDistance: String,
    val avgPace: String,
)