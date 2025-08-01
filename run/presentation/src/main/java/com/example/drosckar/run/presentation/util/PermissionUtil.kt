package com.example.drosckar.run.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

/**
 * Determines whether to show the location permission rationale dialog.
 *
 * Should be called before re-requesting location permissions,
 * typically if the user previously denied it.
 */
fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean {
    return shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
}

/**
 * Determines whether to show the notification permission rationale dialog.
 *
 * This only applies on Android 13+ (API 33).
 */
fun ComponentActivity.shouldShowNotificationPermissionRationale(): Boolean {
    return Build.VERSION.SDK_INT >= 33 &&
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
}

/**
 * Checks whether the given permission is currently granted.
 *
 * @param permission The permission string to check (e.g., Manifest.permission.ACCESS_FINE_LOCATION).
 * @return True if permission is granted, false otherwise.
 */
private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

/**
 * Checks whether the app has fine location permission.
 */
fun Context.hasLocationPermission(): Boolean {
    return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
}

/**
 * Checks whether the app has notification permission.
 * On Android versions below 13, this always returns true.
 */
fun Context.hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= 33) {
        hasPermission(Manifest.permission.POST_NOTIFICATIONS)
    } else true
}