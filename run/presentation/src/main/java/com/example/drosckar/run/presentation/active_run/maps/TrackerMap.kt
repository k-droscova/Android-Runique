package com.example.drosckar.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.core.domain.location.LocationTimestamp
import com.example.drosckar.core.presentation.designsystem.RunIcon
import com.example.drosckar.run.presentation.R
import com.google.maps.android.compose.rememberUpdatedMarkerState

/**
 * Displays a live Google Map tracking the user during an active run.
 *
 * The map animates the camera to follow the user's location, shows a custom animated marker,
 * and applies a custom visual style. When the run is finished, it stops updating and can provide
 * a snapshot of the map if needed.
 *
 * @param isRunFinished Whether the run is marked as completed.
 * @param currentLocation The user's latest known location (nullable).
 * @param locations A list of location segments (each pause/resume group as a list).
 * @param onSnapshot Callback invoked with a Bitmap when a snapshot of the map is taken (used later).
 * @param modifier Optional Modifier for layout purposes.
 */
@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Load custom map style from res/raw/map_style.json
    val mapStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

    // State controlling the camera (position, zoom, etc.)
    val cameraPositionState = rememberCameraPositionState()

    // State controlling the marker (i.e. user’s animated position)
    val markerState = rememberUpdatedMarkerState()

    // Smoothly animate the marker's latitude to the new value over 500ms
    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = "" // Optional label for debugging; can be empty
    )

    // Smoothly animate the marker's longitude to the new value over 500ms
    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.long?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    // Create the LatLng object for Google Maps marker from animated values
    val markerPosition = remember(markerPositionLat, markerPositionLong) {
        LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
    }

    // Update the marker's position in Google Maps only if the run is not finished
    LaunchedEffect(markerPosition, isRunFinished) {
        if(!isRunFinished) {
            markerState.position = markerPosition
        }
    }

    // Animate camera to always center on current location if the run is still active
    LaunchedEffect(currentLocation, isRunFinished) {
        if(currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f) // Zoom level 17 shows buildings/streets clearly
            )
        }
    }

    // The actual Google Map Composable
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = mapStyle // Apply custom JSON-based styling
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false // Disable + / - buttons to prevent zoom manipulation
        )
    ) {
        RuniquePolylines(locations = locations)
        // Show the animated custom marker (only if the run is active and location is valid)
        if(!isRunFinished && currentLocation != null) {
            MarkerComposable(
                currentLocation,
                state = markerState
            ) {
                // Green circular marker with a white running icon centered
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RunIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // TODO: Later add drawing of polylines here based on [locations]
        // TODO: Also implement snapshot capture when [isRunFinished] becomes true
    }
}