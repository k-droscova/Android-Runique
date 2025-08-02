package com.example.drosckar.run.domain

import com.example.drosckar.core.domain.location.LocationWithAltitude
import kotlinx.coroutines.flow.Flow

/**
 * Domain-layer interface for observing device location.
 * Returns a stream of location updates at the given interval.
 */
interface LocationObserver {

    /**
     * Observes the user's location periodically.
     * @param interval Time interval in milliseconds between updates.
     * @return A cold [Flow] emitting [LocationWithAltitude] objects.
     */
    fun observeLocation(interval: Long): Flow<LocationWithAltitude>
}