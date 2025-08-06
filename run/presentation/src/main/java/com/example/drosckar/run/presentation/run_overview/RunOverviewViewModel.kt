package com.example.drosckar.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.core.domain.run.RunRepository
import com.example.drosckar.core.domain.run.SyncRunScheduler
import com.example.drosckar.core.domain.util.SessionStorage
import com.example.drosckar.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

/**
 * ViewModel responsible for managing the UI state and user interactions
 * on the Run Overview screen.
 *
 * Responsibilities:
 * - Observes local runs from [RunRepository] and maps them to UI models ([RunUi]).
 * - Initiates a one-time sync to fetch runs from the remote data source on launch.
 * - Schedules periodic background syncs using [SyncRunScheduler] to keep data fresh.
 * - Handles user actions such as starting a run, deleting a run, and logging out.
 *
 * @property runRepository Repository for accessing and manipulating both local and remote run data.
 *                         Also provides logout and run clearing functionality on user sign-out.
 * @property syncRunScheduler Scheduler responsible for managing background sync jobs (e.g., periodic fetch, retrying unsynced data).
 * @property applicationScope Long-lived coroutine scope used for operations that must survive ViewModel lifecycle (e.g., clearing local DB on logout).
 * @property sessionStorage Interface for managing session state, such as access tokens and the current user ID.
 *                          This is cleared during logout to invalidate the local session.
 */
class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    /**
     * Current UI state of the Run Overview screen.
     * Exposed as a mutable state object and observed by the Composable UI.
     */
    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        // Schedule a periodic background sync using WorkManager.
        // This will continue fetching runs every 30 minutes in the background
        // even if the app is not open, ensuring data is always fresh.
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(30.minutes)
            )
        }

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
            RunOverviewAction.OnLogoutClick -> logout() // Handle logout logic
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

    /**
     * Performs full logout sequence:
     * - Cancels all pending background syncs (e.g. via WorkManager).
     * - Clears the local database (removes all runs).
     * - Invalidates the remote session via the backend.
     * - Clears session storage (removes token + user info).
     *
     * We launch this in [applicationScope] to ensure it's not cancelled
     * if the ViewModel is cleared during navigation.
     */
    private fun logout() {
        applicationScope.launch {
            // Cancel any scheduled background syncs
            syncRunScheduler.cancelAllSyncs()

            // Delete all runs from local DB to clean up space
            runRepository.deleteAllRuns()

            // Invalidate backend session and Ktor auth token
            runRepository.logout()

            // Clear locally cached session tokens and user ID
            sessionStorage.set(null)
        }
    }
}