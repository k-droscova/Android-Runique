package com.example.drosckar.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * ViewModel for handling state and logic of an active running session.
 */
class ActiveRunViewModel : ViewModel() {

    // Current screen state
    var state by mutableStateOf(ActiveRunState())
        private set

    // One-time events (e.g., showing a snackbar or navigating away)
    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    /**
     * Handles user actions from the UI. Currently not implemented.
     */
    fun onAction(action: ActiveRunAction) {
        // TODO: Implement logic for start/stop/pause/save tracking
    }
}