package com.example.drosckar.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.example.drosckar.core.domain.location.LocationTimestamp
import kotlin.math.abs

/**
 * Utility object for calculating color gradients of polylines based on running speed.
 */
object PolylineColorCalculator {

    /**
     * Computes the appropriate color for the line between two [LocationTimestamp]s
     * based on the runner's speed in that interval.
     *
     * @param location1 Starting location timestamp.
     * @param location2 Ending location timestamp.
     * @return A [Color] representing the speed (green = slow, red = fast).
     */
    fun locationsToColor(location1: LocationTimestamp, location2: LocationTimestamp): Color {
        // Distance in meters between two GPS points
        val distanceMeters = location1.location.location.distanceTo(location2.location.location)

        // Time in seconds between the two timestamps
        val timeDiff = abs(
            (location2.durationTimestamp - location1.durationTimestamp).inWholeSeconds
        )

        // Calculate speed in km/h (distance [m] / time [s] * 3.6)
        val speedKmh = (distanceMeters / timeDiff) * 3.6

        // Interpolate the color based on the computed speed
        return interpolateColor(
            speedKmh = speedKmh,
            minSpeed = 5.0,    // Walking speed
            maxSpeed = 20.0,   // Fast running speed
            colorStart = Color.Green,
            colorMid = Color.Yellow,
            colorEnd = Color.Red
        )
    }

    /**
     * Interpolates between green, yellow, and red depending on how fast the user is running.
     *
     * - Green for slow speeds (≤ minSpeed)
     * - Yellow for medium speeds (around midpoint)
     * - Red for fast speeds (≥ maxSpeed)
     *
     * @param speedKmh The current speed in kilometers per hour.
     * @param minSpeed Speed at which the color should be fully green.
     * @param maxSpeed Speed at which the color should be fully red.
     * @param colorStart Color representing minimum speed (green).
     * @param colorMid Color representing medium speed (yellow).
     * @param colorEnd Color representing maximum speed (red).
     * @return A blended [Color] between green → yellow → red.
     */
    private fun interpolateColor(
        speedKmh: Double,
        minSpeed: Double,
        maxSpeed: Double,
        colorStart: Color,
        colorMid: Color,
        colorEnd: Color
    ): Color {
        // Normalize speed to a [0.0, 1.0] range
        val ratio = ((speedKmh - minSpeed) / (maxSpeed - minSpeed)).coerceIn(0.0..1.0)

        // Determine which two colors to blend and compute the ratio for blending
        val colorInt = if (ratio <= 0.5) {
            val midRatio = ratio / 0.5 // Normalize to [0.0, 1.0]
            ColorUtils.blendARGB(
                colorStart.toArgb(),
                colorMid.toArgb(),
                midRatio.toFloat()
            )
        } else {
            val midToEndRatio = (ratio - 0.5) / 0.5 // Normalize to [0.0, 1.0]
            ColorUtils.blendARGB(
                colorMid.toArgb(),
                colorEnd.toArgb(),
                midToEndRatio.toFloat()
            )
        }

        // Return the final composed color
        return Color(colorInt)
    }
}