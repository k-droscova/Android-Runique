package com.example.drosckar.run.presentation.active_run.maps

import androidx.compose.ui.graphics.Color
import com.example.drosckar.core.domain.location.Location

/**
 * Represents a visual line between two geographic [Location]s on the map,
 * colored based on the user's speed between those two points.
 *
 * @property location1 The starting location of this segment.
 * @property location2 The ending location of this segment.
 * @property color The color of the line between the two points (based on speed).
 */
data class PolylineUi(
    val location1: Location,
    val location2: Location,
    val color: Color
)