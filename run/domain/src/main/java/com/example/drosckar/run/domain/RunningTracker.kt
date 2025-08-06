@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.drosckar.run.domain

import com.example.drosckar.run.domain.LocationDataCalculator
import com.example.drosckar.core.domain.Timer
import com.example.drosckar.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Central class responsible for tracking a running session.
 *
 * Maintains state flows for:
 * - Elapsed time
 * - Tracking status
 * - Real-time location updates
 * - Calculated run data (distance, pace, polyline)
 *
 * Designed to be used with a foreground service, not a ViewModel, so that
 * state persists even if the app is closed from recents.
 */
class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    // State representing the current aggregated data of the run
    private val _runData = MutableStateFlow(RunData())
    val runData = _runData.asStateFlow()

    // Whether we're actively tracking a run (used to trigger timer + data collection)
    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    // Mutable flag controlling whether we are tracking the user's location
    private val isObservingLocation = MutableStateFlow(false)

    // Total time elapsed in the current run
    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

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

    init {
        // Track elapsed time only when actively tracking a run
        _isTracking
            // when switch from true to false, add empty list to the end of the list
            .onEach { isTracking ->
                if(!isTracking) {
                    val newList = buildList {
                        addAll(runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        it.copy(locations = newList)
                    }
                }
            }
            .flatMapLatest { isTracking ->
                if(isTracking) {
                    Timer.timeAndEmit()
                } else flowOf()
            }
            .onEach {
                _elapsedTime.value += it
            }
            .launchIn(applicationScope)

        // Chain of flow operators to track user path and update metrics reactively
        currentLocation
            .filterNotNull() // Ignore null locations
            .combineTransform(_isTracking) { location, isTracking ->
                if(isTracking) {
                    emit(location)
                }
            }
            .combine(_elapsedTime) { location, elapsedTime ->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }
            .onEach { location ->
                // Add the new location to the latest polyline segment
                val currentLocations = runData.value.locations
                val lastLocationsList = if(currentLocations.isNotEmpty()) {
                    currentLocations.last() + location
                } else listOf(location)
                val newLocationsList = currentLocations.replaceLast(lastLocationsList)

                // Distance calculation using utility
                val distanceMeters = LocationDataCalculator.getTotalDistanceMeters(
                    locations = newLocationsList
                )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = location.durationTimestamp

                // Avoid division by zero
                val avgSecondsPerKm = if(distanceKm == 0.0) {
                    0
                } else {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                }

                // Update observable run data state
                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationsList
                    )
                }
            }
            .launchIn(applicationScope)
    }

    /**
     * Starts or stops run tracking.
     * When set to true, starts accumulating time and updating run data.
     */
    fun setIsTracking(isTracking: Boolean) {
        this._isTracking.value = isTracking
    }

    /** Begin passive location observation (e.g. for displaying user position on map). */
    fun startObservingLocation() {
        isObservingLocation.value = true
    }

    /** Stop passive location observation. */
    fun stopObservingLocation() {
        isObservingLocation.value = false
    }

    fun finishRun() {
        stopObservingLocation()
        setIsTracking(false)
        _elapsedTime.value = Duration.ZERO
        _runData.value = RunData()
    }
}

/**
 * Replaces the last list in a list-of-lists with a new list.
 *
 * Used to update the last polyline with the newest tracked point.
 */
private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    if (this.isEmpty()) return listOf(replacement)
    return this.dropLast(1) + listOf(replacement)
}