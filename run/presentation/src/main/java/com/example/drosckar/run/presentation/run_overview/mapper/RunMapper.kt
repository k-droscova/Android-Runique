package com.example.drosckar.run.presentation.run_overview.mapper

import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.presentation.ui.formatted
import com.example.drosckar.core.presentation.ui.toFormattedKm
import com.example.drosckar.core.presentation.ui.toFormattedKmh
import com.example.drosckar.core.presentation.ui.toFormattedMeters
import com.example.drosckar.core.presentation.ui.toFormattedPace
import com.example.drosckar.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Maps the [Run] domain model to a [RunUi] model for display in the UI.
 *
 * Converts raw values like distances and durations into formatted strings, and
 * converts UTC time to the user's system time zone.
 */
fun Run.toRunUi(): RunUi {
    // Convert UTC time to user's system time zone
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())

    // Format date as "Aug 01, 2025 - 07:30PM"
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    // Convert meters to kilometers (e.g., 1500 -> 1.5 km)
    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id!!, // Must not be null by this point
        duration = duration.formatted(), // e.g., "00:45:23"
        dateTime = formattedDateTime,    // e.g., "Aug 01, 2025 - 07:30PM"
        distance = distanceKm.toFormattedKm(), // e.g., "1.5 km"
        avgSpeed = avgSpeedKmh.toFormattedKmh(), // e.g., "10.3 km/h"
        maxSpeed = maxSpeedKmh.toFormattedKmh(), // e.g., "15.0 km/h"
        pace = duration.toFormattedPace(distanceKm), // e.g., "05:30 min/km"
        totalElevation = totalElevationMeters.toFormattedMeters(), // e.g., "120 m"
        mapPictureUrl = mapPictureUrl
    )
}