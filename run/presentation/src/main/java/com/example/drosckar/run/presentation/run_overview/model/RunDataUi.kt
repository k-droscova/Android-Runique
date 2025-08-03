package com.example.drosckar.run.presentation.run_overview.model

/**
 * UI model representing a single metric to be shown in the data grid (e.g., distance, pace).
 *
 * @param name Name of the metric (e.g., "Distance").
 * @param value Formatted value of the metric (e.g., "3.5 km").
 */
data class RunDataUi(
    val name: String,
    val value: String
)