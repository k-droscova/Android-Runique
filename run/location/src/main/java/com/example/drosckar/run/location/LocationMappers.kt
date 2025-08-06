package com.example.drosckar.run.location

import android.location.Location
import com.example.drosckar.core.domain.location.LocationWithAltitude

/**
 * Extension function to map Android's [Location] object to domain-level [LocationWithAltitude].
 */
fun Location.toLocationWithAltitude(): LocationWithAltitude {
    return LocationWithAltitude(
        location = com.example.drosckar.core.domain.location.Location(
            lat = latitude,
            long = longitude
        ),
        altitude = altitude
    )
}