package com.example.drosckar.run.presentation.active_run

import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.run.domain.RunData
import kotlin.time.Duration

/**
 * Represents the full UI state of the Active Run screen.
 *
 * Contains all observable properties needed to render the screen
 * and control the behavior of the running session.
 */
data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO, // Total run time tracked
    val runData: RunData = RunData(), // Distance, pace, and route data
    val shouldTrack: Boolean = false, // Whether location tracking is active
    val hasStartedRunning: Boolean = false, // Whether the run was started at all
    val currentLocation: Location? = null, // Most recent location update
    val isRunFinished: Boolean = false, // Whether the run has been marked as finished
    val isSavingRun: Boolean = false, // Whether the run is currently being saved to storage
    val showLocationRationale: Boolean = false, // Whether to show location permission rationale dialog
    val showNotificationRationale: Boolean = false // Whether to show notification permission rationale dialog
)