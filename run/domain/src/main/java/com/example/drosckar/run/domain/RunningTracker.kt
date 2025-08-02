@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.drosckar.run.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/**
 * Central tracker for running sessions.
 * Observes user location and exposes it reactively using [StateFlow].
 */
class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    // Mutable flag controlling whether we are tracking the user's location
    private val isObservingLocation = MutableStateFlow(false)

    /**
     * Public observable stream of the user's current location, updated every second.
     * - If tracking is enabled, emits new locations every 1000ms.
     * - Otherwise, emits nothing (empty flow).
     */
    val currentLocation = isObservingLocation
        .flatMapLatest { isObserving ->
            if (isObserving) {
                locationObserver.observeLocation(1000L) // Start streaming location
            } else {
                flowOf() // Emit nothing if not observing
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )

    /** Starts streaming the user's location. */
    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    /** Stops location tracking. */
    fun stopObservingLocation() {
        isObservingLocation.value = false
    }
}