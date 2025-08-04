package com.example.drosckar.run.network.mappers

import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.run.network.util.CreateRunRequest
import com.example.drosckar.run.network.dto.RunDto
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

/**
 * Maps a [RunDto] (API model) to a [Run] domain model.
 */
fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(lat, long),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

/**
 * Maps a [Run] domain model to a [CreateRunRequest] used for syncing to the backend.
 *
 * Note: This excludes `mapPictureUrl` and converts datetime to epoch millis.
 */
fun Run.toCreateRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        id = id!!, // assert not null: should already be saved locally
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        lat = location.lat,
        long = location.long,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        epochMillis = dateTimeUtc.toEpochSecond() * 1000L
    )
}