package com.example.drosckar.run.domain

import com.example.drosckar.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

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
     * Each polyline is a list of [com.example.drosckar.core.domain.location.LocationTimestamp] entries representing a segment of the run.
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

    fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOf { locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                val distance = location1.location.location.distanceTo(
                    other = location2.location.location
                )
                val hoursDifference = (location2.durationTimestamp - location1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)

                if(hoursDifference == 0.0) {
                    0.0
                } else {
                    (distance / 1000.0) / hoursDifference
                }
            }.maxOrNull() ?: 0.0
        }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { locationSet ->
            locationSet.zipWithNext { location1, location2 ->
                val altitude1 = location1.location.altitude
                val altitude2 = location2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }
}