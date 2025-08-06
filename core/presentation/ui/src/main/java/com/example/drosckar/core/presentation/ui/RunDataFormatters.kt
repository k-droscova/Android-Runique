package com.example.drosckar.core.presentation.ui

import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

/**
 * Formats a [Duration] into a human-readable time string in the format HH:MM:SS.
 * Always uses two digits for each unit, with leading zeroes if necessary.
 *
 * Example: `1 hour, 5 minutes, 9 seconds` -> `"01:05:09"`
 */
fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = String.format(Locale.US, "%02d", totalSeconds / 3600)
    val minutes = String.format(Locale.US, "%02d", (totalSeconds % 3600) / 60)
    val seconds = String.format(Locale.US, "%02d", totalSeconds % 60)

    return "$hours:$minutes:$seconds"
}

/**
 * Formats a distance in kilometers to one decimal place.
 *
 * Example: `3.456` -> `"3.5 km"`
 */
fun Double.toFormattedKm(): String {
    return "${this.roundToDecimals(1)} km"
}

/**
 * Calculates and formats the average pace per kilometer from a [Duration] and total distance in kilometers.
 * If either duration is zero or distance is invalid (≤ 0), returns a placeholder dash (`"-"`).
 *
 * Output format: `MM:SS / km`
 *
 * Example: `Duration(300s), distance = 1.0` -> `"05:00 / km"`
 */
fun Duration.toFormattedPace(distanceKm: Double): String {
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }

    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val avgPaceMinutes = secondsPerKm / 60
    val avgPaceSeconds = String.format(Locale.US, "%02d", secondsPerKm % 60)

    return "$avgPaceMinutes:$avgPaceSeconds / km"
}

/**
 * Converts a [Double] representing speed (km/h) to a formatted string rounded to 1 decimal place.
 *
 * Example: `10.324` -> `"10.3 km/h"`
 */
fun Double.toFormattedKmh(): String {
    return "${roundToDecimals(1)} km/h"
}

/**
 * Converts an [Int] representing distance (meters) to a formatted string.
 *
 * Example: `150` -> `"150 m"`
 */
fun Int.toFormattedMeters(): String {
    return "$this m"
}

/**
 * Rounds a [Double] to a specified number of decimal places.
 *
 * @param decimalCount The number of decimal places to round to.
 * @return The rounded [Double] value.
 */
private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}