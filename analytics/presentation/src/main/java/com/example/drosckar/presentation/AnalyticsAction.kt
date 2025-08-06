package com.example.drosckar.presentation

/**
 * Represents all user actions that can be performed on the Analytics Dashboard screen.
 *
 * Keeping user interactions in a sealed interface allows the [AnalyticsDashboardViewModel]
 * or navigation logic to handle them in a type-safe and extensible way.
 */
sealed interface AnalyticsAction {

    /**
     * Called when the user presses the back button in the toolbar to navigate back.
     */
    data object OnBackClick : AnalyticsAction
}