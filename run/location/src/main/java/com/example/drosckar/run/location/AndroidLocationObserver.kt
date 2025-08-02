package com.example.drosckar.run.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.drosckar.core.domain.location.LocationWithAltitude
import com.example.drosckar.run.domain.LocationObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Platform-specific implementation of [LocationObserver] using FusedLocationProviderClient.
 */
class AndroidLocationObserver(
    private val context: Context
) : LocationObserver {

    // Android's fused location provider used to request location updates.
    private val client = LocationServices.getFusedLocationProviderClient(context)

    override fun observeLocation(interval: Long): Flow<LocationWithAltitude> = callbackFlow {
        // System service to check if GPS/network is enabled
        val locationManager = context.getSystemService<LocationManager>()!!

        var isGpsEnabled = false
        var isNetworkEnabled = false

        // Wait until at least one location provider is enabled
        while (!isGpsEnabled && !isNetworkEnabled) {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                delay(3000L) // Wait before checking again
            }
        }

        // Ensure we have necessary location permissions
        if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            close() // Don't start location updates if permissions are missing
        } else {
            // Emit last known cached location immediately
            client.lastLocation.addOnSuccessListener {
                it?.let { location ->
                    trySend(location.toLocationWithAltitude())
                }
            }

            // Build request for continuous location updates
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).build()

            // Callback that triggers with every new location
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        trySend(location.toLocationWithAltitude()) // Send latest update to Flow
                    }
                }
            }

            // Request ongoing location updates
            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            // When flow collection is cancelled, stop receiving location updates
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }
}