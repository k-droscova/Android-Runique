package com.example.drosckar.run.presentation.active_run

/**
 * Represents user-driven actions that can be triggered from the Active Run screen.
 * These actions are handled in the ViewModel to update state or trigger effects.
 */
sealed interface ActiveRunAction {

    /** User tapped to start or pause tracking the run. */
    data object OnToggleRunClick : ActiveRunAction

    /** User tapped to finish and save the run. */
    data object OnFinishRunClick : ActiveRunAction

    /** User tapped to resume a paused run. */
    data object OnResumeRunClick : ActiveRunAction

    /** User tapped the back button. */
    data object OnBackClick : ActiveRunAction

    /**
     * Called when the location permission request completes.
     *
     * @param acceptedLocationPermission Whether the permission was granted.
     * @param showLocationRationale Whether to show a rationale dialog before re-requesting.
     */
    data class SubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationRationale: Boolean
    ) : ActiveRunAction

    /**
     * Called when the notification permission request completes.
     *
     * @param acceptedNotificationPermission Whether the permission was granted.
     * @param showNotificationPermissionRationale Whether to show a rationale dialog.
     */
    data class SubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationPermissionRationale: Boolean
    ) : ActiveRunAction

    /** Dismiss the rationale dialog after user interaction. */
    data object DismissRationaleDialog : ActiveRunAction
}