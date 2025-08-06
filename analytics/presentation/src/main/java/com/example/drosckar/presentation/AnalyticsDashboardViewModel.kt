package com.example.drosckar.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel responsible for exposing the state of the Analytics Dashboard screen.
 *
 * - Holds a nullable state, which allows the UI to show a loading indicator
 *   while the analytics data is being prepared or fetched.
 * - Can be extended to fetch analytics from a local DB or repository.
 *
 * In this initial setup, the ViewModel is simple and does not perform any computation.
 * Future improvements may include calculating state values from real run data,
 * caching, and offline support.
 */
class AnalyticsDashboardViewModel : ViewModel() {

    /**
     * The current UI state of the analytics screen.
     *
     * - When `null`, the UI should show a loading indicator.
     * - When non-null, the UI renders the analytics cards.
     *
     * Default is `null` to simplify preview logic and simulate loading behavior.
     */
    var state by mutableStateOf<AnalyticsDashboardState?>(null)
        private set
}