package com.example.drosckar.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

/**
 * ViewModel for handling business logic and UI state of the Active Run screen.
 *
 * Manages user actions, state updates, and one-off UI events such as dialogs or navigation.
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

    /** Whether the app currently has location permission. */
    private val _hasLocationPermission = MutableStateFlow(false)

    init {
        // Start or stop observing location depending on permission status
        _hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission) {
                    runningTracker.startObservingLocation()
                } else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        // Log current location whenever it updates
        runningTracker
            .currentLocation
            .onEach { location ->
                Timber.d("New location: $location")
            }
            .launchIn(viewModelScope)
    }


    /**
     * Handles user-triggered actions from the UI.
     *
     * @param action The user action to handle.
     */
    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {
                // TODO: Handle run completion
            }

            ActiveRunAction.OnResumeRunClick -> {
                // TODO: Handle resuming a paused run
            }

            ActiveRunAction.OnToggleRunClick -> {
                // TODO: Handle toggling start/pause tracking
            }

            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = action.acceptedLocationPermission
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

            else -> Unit
        }
    }
}