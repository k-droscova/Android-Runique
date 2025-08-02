package com.example.drosckar.core.domain

import com.example.drosckar.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt

/**
 * Utility object for computing metrics from location data.
 *
 * Currently provides functions to calculate:
 * - Total distance in meters based on tracked GPS points
 */
object LocationDataCalculator {

    /**
     * Calculates the total distance (in meters) from a list of location polylines.
     *
     * Each polyline is a list of [LocationTimestamp] entries representing a segment of the run.
     * The distance is calculated between each consecutive pair of locations within each polyline.
     *
     * @param locations A list of polylines, where each polyline is a list of timestamped locations.
     * @return Total distance of all polylines in meters, rounded to the nearest integer.
     */
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { timestampsPerLine ->
            // For each polyline segment (a list of LocationTimestamps),
            // zip consecutive pairs (e.g., [a, b, c] -> [(a,b), (b,c)])
            timestampsPerLine.zipWithNext { location1, location2 ->
                // For each pair, extract raw Location objects and compute the distance between them
                location1.location.location.distanceTo(location2.location.location)
            }
                // Sum all the segment distances within this polyline
                .sum()
                // Round the float total to an integer (meters)
                .roundToInt()
        }
    }
}