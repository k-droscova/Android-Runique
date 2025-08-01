package com.example.drosckar.run.presentation.run_overview

import androidx.lifecycle.ViewModel

/**
 * ViewModel responsible for managing the state and logic of the Run Overview screen.
 *
 * Currently a placeholder with no state or logic implemented.
 * Future responsibilities may include:
 * - Tracking and exposing the list of past runs
 * - Handling user actions like starting a run, logging out, or viewing analytics
 * - Coordinating with repositories to fetch and update run-related data
 */
class RunOverviewViewModel : ViewModel() {

    /**
     * Handles [RunOverviewAction] events triggered from the UI.
     *
     * @param action The user action to process.
     *
     * TODO:
     * - Implement start run flow
     * - Implement logout functionality
     * - Implement navigation to analytics
     */
    fun onAction(action: RunOverviewAction) {
        // Not yet implemented
    }
}