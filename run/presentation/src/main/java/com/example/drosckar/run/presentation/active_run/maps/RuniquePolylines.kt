package com.example.drosckar.run.presentation.active_run.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.drosckar.core.domain.location.LocationTimestamp
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Polyline

/**
 * Composable that draws polylines onto a Google Map based on the provided location history.
 *
 * Each polyline segment connects two consecutive points from a paused/resumed run segment
 * and is colored based on the speed between those two points (slow = green, fast = red).
 *
 * @param locations A list of segments, each containing a list of [LocationTimestamp]s.
 */
@Composable
fun RuniquePolylines(locations: List<List<LocationTimestamp>>) {
    // Convert each segment into a list of PolylineUi elements connecting adjacent timestamps
    val polylines = remember(locations) {
        locations.map { segment ->
            segment.zipWithNext { timestamp1, timestamp2 ->
                PolylineUi(
                    location1 = timestamp1.location.location,
                    location2 = timestamp2.location.location,
                    color = PolylineColorCalculator.locationsToColor(
                        location1 = timestamp1,
                        location2 = timestamp2
                    )
                )
            }
        }
    }

    // Draw each polyline segment individually to allow custom colors
    polylines.forEach { segment ->
        segment.forEach { polylineUi ->
            Polyline(
                points = listOf(
                    LatLng(polylineUi.location1.lat, polylineUi.location1.long),
                    LatLng(polylineUi.location2.lat, polylineUi.location2.long)
                ),
                color = polylineUi.color,
                jointType = JointType.BEVEL // Rounded joins look cleaner around corners
            )
        }
    }
}