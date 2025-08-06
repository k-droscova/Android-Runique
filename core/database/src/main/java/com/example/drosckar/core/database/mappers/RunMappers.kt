package com.example.drosckar.core.database.mappers

import com.example.drosckar.core.database.entity.RunEntity
import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

/**
 * Converts a `RunEntity` from the database into a domain model `Run`.
 * Useful to isolate Room-specific logic from domain logic.
 */
fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(lat = latitude, long = longitude),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

/**
 * Converts a domain model `Run` into a `RunEntity` for database storage.
 * Ensures proper serialization of types like Duration and ZonedDateTime.
 */
fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(), // Auto-generate ID if not present
        durationMillis = duration.inWholeMilliseconds,
        maxSpeedKmh = maxSpeedKmh,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        latitude = location.lat,
        longitude = location.long,
        distanceMeters = distanceMeters,
        avgSpeedKmh = avgSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}