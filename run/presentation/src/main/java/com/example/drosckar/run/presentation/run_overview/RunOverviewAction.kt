package com.example.drosckar.run.presentation.run_overview

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
}