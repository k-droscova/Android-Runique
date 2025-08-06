package com.example.drosckar.analytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.analytics.domain.AnalyticsRepository
import com.example.drosckar.analytics.presentation.mappers.toAnalyticsDashboardState
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for exposing the state of the Analytics Dashboard screen.
 *
 * - Holds a nullable state that becomes non-null after data is loaded.
 * - On initialization, it fetches analytics values from the repository
 *   and maps them to the UI model ([AnalyticsDashboardState]).
 *
 * This ViewModel can later be extended to support refresh or error handling.
 *
 * @param analyticsRepository Provides access to calculated analytics values.
 */
class AnalyticsDashboardViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    /**
     * State representing the current analytics dashboard data to display.
     *
     * - `null` means loading or empty.
     * - Non-null values represent a fully loaded dashboard.
     */
    var state by mutableStateOf<AnalyticsDashboardState?>(null)
        private set

    init {
        // Load analytics values on ViewModel creation
        viewModelScope.launch {
            state = analyticsRepository
                .getAnalyticsValues()
                .toAnalyticsDashboardState()
        }
    }
}