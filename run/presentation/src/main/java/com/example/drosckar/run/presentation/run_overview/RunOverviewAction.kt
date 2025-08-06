package com.example.drosckar.run.presentation.run_overview

import com.example.drosckar.run.presentation.run_overview.model.RunUi

/**
 * Represents user interactions (UI events) on the Run Overview screen.
 *
 * This sealed interface helps the [RunOverviewViewModel] handle different user actions.
 */
sealed interface RunOverviewAction {

    /**
     * Called when the user taps the "Start" button to begin a new run.
     */
    data object OnStartClick : RunOverviewAction

    /**
     * Called when the user taps the logout button from a menu or toolbar.
     */
    data object OnLogoutClick : RunOverviewAction

    /**
     * Called when the user taps to view analytics or statistics of their past runs.
     */
    data object OnAnalyticsClick : RunOverviewAction

    /**
     * Called when the user deletes a specific run from the list.
     *
     * @param runUi The UI model of the run to delete.
     *
     * This action triggers the removal of the run both locally and remotely,
     * via [RunOverviewViewModel] and [RunRepository.deleteRun].
     */
    data class DeleteRun(val runUi: RunUi): RunOverviewAction

}