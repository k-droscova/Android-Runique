package com.example.drosckar.run.presentation.active_run

/**
 * User actions that can be triggered from the Active Run screen.
 */
sealed interface ActiveRunAction {
    /** User tapped start or pause (toggle tracking). */
    data object OnToggleRunClick: ActiveRunAction

    /** User tapped to finish and save the run. */
    data object OnFinishRunClick: ActiveRunAction

    /** User resumed a paused run. */
    data object OnResumeRunClick: ActiveRunAction

    /** User tapped the back button. */
    data object OnBackClick: ActiveRunAction
}