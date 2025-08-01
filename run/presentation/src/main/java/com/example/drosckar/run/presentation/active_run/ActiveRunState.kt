package com.example.drosckar.run.presentation.active_run

import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.run.domain.RunData
import kotlin.time.Duration

/**
 * Represents the full UI state of the Active Run screen.
 */
data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO, // Total time tracked
    val runData: RunData = RunData(), // Distance, pace, and location data
    val shouldTrack: Boolean = false, // Whether tracking is active (i.e., running)
    val hasStartedRunning: Boolean = false, // Whether the run has ever started
    val currentLocation: Location? = null, // Most recent location update
    val isRunFinished: Boolean = false, // Whether the run was completed
    val isSavingRun: Boolean = false // Whether the run is currently being saved
)