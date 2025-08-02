package com.example.drosckar.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

/**
 * ViewModel responsible for managing the business logic and UI state of the Active Run screen.
 *
 * It handles:
 * - Managing state for tracking runs (paused/running/resumed).
 * - Synchronizing UI state with `RunningTracker` data (location, elapsed time, run metrics).
 * - Handling UI actions triggered by the user.
 * - Permission management (location access).
 */
class ActiveRunViewModel(
    private val runningTracker: RunningTracker
) : ViewModel() {

    /** Current observable state of the Active Run screen. */
    var state by mutableStateOf(ActiveRunState())
        private set

    /** Channel for one-time UI events (e.g., showing snackbar, navigation). */
    private val eventChannel = Channel<ActiveRunEvent>()

    /** Publicly exposed flow of one-time events. */
    val events = eventChannel.receiveAsFlow()

    // --- STATE FLOW DERIVATIONS ---

    /**
     * Flow that reflects the current value of [state.shouldTrack], emitting updates
     * whenever it changes.
     *
     * `snapshotFlow` bridges Compose state to a coroutine Flow.
     * We use `stateIn` to convert it into a hot StateFlow for composition.
     */
    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = state.shouldTrack
        )

    /** Flow that holds the current status of whether location permissions are granted. */
    private val hasLocationPermission = MutableStateFlow(false)

    /**
     * Combined state that determines if we should track the user's run:
     * - The user *intends* to track (`shouldTrack`)
     * - The app *has permission* to do so (`hasLocationPermission`)
     *
     * If either is false, location tracking is turned off.
     */
    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    // --- INITIALIZER ---

    init {
        // Observe permission changes and start/stop location observation accordingly
        hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        // Update the tracking state in the tracker based on the derived isTracking flow
        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        // Listen for location updates and push them into UI state
        runningTracker
            .currentLocation
            .onEach { location ->
                Timber.d("New location: $location")
                state = state.copy(currentLocation = location?.location)
            }
            .launchIn(viewModelScope)

        // Listen for run data updates (distance, pace, recorded path) and update UI state
        runningTracker
            .runData
            .onEach {
                state = state.copy(runData = it)
            }
            .launchIn(viewModelScope)

        // Listen for elapsed time updates and update UI state
        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }
            .launchIn(viewModelScope)
    }

    // --- UI ACTION HANDLING ---

    /**
     * Handles user-triggered actions from the UI.
     *
     * @param action The user action to handle.
     */
    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                // TODO: Save run, capture map, and navigate away
            }

            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(shouldTrack = true)
            }

            ActiveRunAction.OnBackClick -> {
                state = state.copy(shouldTrack = false)
            }

            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }

            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationPermissionRationale
                )
            }

            is ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showNotificationRationale = false,
                    showLocationRationale = false
                )
            }
        }
    }
}