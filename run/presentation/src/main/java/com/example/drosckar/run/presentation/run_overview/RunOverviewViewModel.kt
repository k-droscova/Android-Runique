package com.example.drosckar.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.core.domain.run.RunRepository
import com.example.drosckar.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the UI state and user interactions
 * on the Run Overview screen.
 *
 * Responsibilities:
 * - Observes local runs from [RunRepository] and maps them to UI models ([RunUi]).
 * - Initiates a sync with the remote data source upon initialization to fetch fresh runs.
 * - Handles user actions like starting a new run, viewing analytics, logging out,
 *   and deleting a run.
 *
 * @property runRepository Repository used to interact with both local and remote run data sources.
 */
class RunOverviewViewModel(
    private val runRepository: RunRepository
) : ViewModel() {

    /**
     * Current UI state of the Run Overview screen.
     * Exposed as a mutable state object and observed by the Composable UI.
     */
    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        // Start observing local database for real-time updates to the list of runs.
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() } // Convert domain models to UI models
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        // Sync pending runs and fetch fresh runs from the remote API on startup (one-time).
        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    /**
     * Handles all user actions (events) triggered from the UI.
     *
     * @param action The [RunOverviewAction] that represents a user event.
     */
    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> Unit // Handle logout logic elsewhere
            RunOverviewAction.OnStartClick -> Unit   // Navigation to active run screen is handled in Composable
            is RunOverviewAction.DeleteRun -> {
                // Delete the selected run both locally and remotely
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            else -> Unit
        }
    }
}