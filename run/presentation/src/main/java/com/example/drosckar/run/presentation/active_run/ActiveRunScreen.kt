@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.drosckar.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.core.presentation.designsystem.StartIcon
import com.example.drosckar.core.presentation.designsystem.StopIcon
import com.example.drosckar.core.presentation.designsystem.components.RuniqueActionButton
import com.example.drosckar.core.presentation.designsystem.components.RuniqueDialog
import com.example.drosckar.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.example.drosckar.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.example.drosckar.core.presentation.designsystem.components.RuniqueScaffold
import com.example.drosckar.core.presentation.designsystem.components.RuniqueToolbar
import com.example.drosckar.run.presentation.R
import com.example.drosckar.run.presentation.active_run.components.RunDataCard
import com.example.drosckar.run.presentation.active_run.maps.TrackerMap
import com.example.drosckar.run.presentation.active_run.service.ActiveRunService
import com.example.drosckar.run.presentation.util.hasLocationPermission
import com.example.drosckar.run.presentation.util.hasNotificationPermission
import com.example.drosckar.run.presentation.util.shouldShowLocationPermissionRationale
import com.example.drosckar.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

/**
 * Root Composable for the Active Run screen.
 * Injects ViewModel and handles one-time UI events like back press.
 */
@Composable
fun ActiveRunScreenRoot(
    onBackClick: () -> Unit,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            // Handle back navigation only if user hasn't started running yet
            if (action is ActiveRunAction.OnBackClick && !viewModel.state.hasStartedRunning) {
                onBackClick()
            }
            viewModel.onAction(action)
        }
    )
}

/**
 * Main layout for the Active Run screen.
 *
 * Displays the top bar, floating action button (FAB), and a card showing run data.
 * Also handles runtime permission requests (location and notification) and
 * conditionally shows rationale dialogs if needed.
 *
 * @param state Current UI state of the active run.
 * @param onAction Callback for dispatching user actions (e.g., permission grants, toggling run).
 */
@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current

    // Launcher for requesting multiple permissions at once.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        handlePermissionResults(perms, context, onAction)
    }

    // On first composition, determine initial permission states and launch requests if needed.
    LaunchedEffect(key1 = true) {
        initializePermissionState(context, onAction, permissionLauncher)
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if(state.isRunFinished) {
            onServiceToggle(false)
        }
    }

    LaunchedEffect(key1 = state.shouldTrack) {
        if(context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    RuniqueScaffold(
        withGradient = false,
        topAppBar = {
            RuniqueToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                },
            )
        },
        floatingActionButton = {
            RuniqueFloatingActionButton(
                icon = if (state.shouldTrack) {
                    StopIcon
                } else {
                    StartIcon
                },
                onClick = {
                    onAction(ActiveRunAction.OnToggleRunClick)
                },
                iconSize = 20.dp,
                contentDescription = if(state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = {},
                modifier = Modifier
                    .fillMaxSize()
            )
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(padding)
                    .fillMaxWidth()
            )
        }
    }

    // Show dialog when the run is paused after it has started
    PauseRunDialog(
        isPaused = !state.shouldTrack && state.hasStartedRunning,
        isSavingRun = state.isSavingRun,
        onResumeClick = { onAction(ActiveRunAction.OnResumeRunClick) },
        onFinishClick = { onAction(ActiveRunAction.OnFinishRunClick) }
    )

    // Show rationale dialog if any permission rationale is required.
    PermissionRationaleDialog(
        showLocationRationale = state.showLocationRationale,
        showNotificationRationale = state.showNotificationRationale,
        onOkayClick = {
            onAction(ActiveRunAction.DismissRationaleDialog)
            permissionLauncher.requestRuniquePermissions(context)
        }
    )
}

/**
 * Dialog displayed when the run is paused after being started.
 *
 * Offers the user a choice to either resume or finish the run.
 *
 * @param isPaused Whether the run is currently paused (but has started).
 * @param isSavingRun Whether the run is currently being saved (to show loading on "Finish").
 * @param onResumeClick Callback when user chooses to resume the run.
 * @param onFinishClick Callback when user chooses to finish and save the run.
 */
@Composable
private fun PauseRunDialog(
    isPaused: Boolean,
    isSavingRun: Boolean,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    if (!isPaused) return

    RuniqueDialog(
        title = stringResource(id = R.string.running_is_paused),
        onDismiss = onResumeClick, // Auto-resume if user tries to dismiss manually
        description = stringResource(id = R.string.resume_or_finish_run),
        primaryButton = {
            RuniqueActionButton(
                text = stringResource(id = R.string.resume),
                isLoading = false,
                onClick = onResumeClick,
                modifier = Modifier.weight(1f)
            )
        },
        secondaryButton = {
            RuniqueOutlinedActionButton(
                text = stringResource(id = R.string.finish),
                isLoading = isSavingRun,
                onClick = onFinishClick,
                modifier = Modifier.weight(1f)
            )
        }
    )
}

/**
 * Dialog displayed when the user needs to understand why location or notification permissions are required.
 *
 * Only shown if one or both rationale flags are true.
 *
 * @param showLocationRationale Whether to show location permission rationale.
 * @param showNotificationRationale Whether to show notification permission rationale.
 * @param onOkayClick Action to take when user agrees to grant permissions.
 */
@Composable
private fun PermissionRationaleDialog(
    showLocationRationale: Boolean,
    showNotificationRationale: Boolean,
    onOkayClick: () -> Unit
) {
    if (!showLocationRationale && !showNotificationRationale) return

    val description = when {
        showLocationRationale && showNotificationRationale -> {
            stringResource(id = R.string.location_notification_rationale)
        }
        showLocationRationale -> {
            stringResource(id = R.string.location_rationale)
        }
        else -> {
            stringResource(id = R.string.notification_rationale)
        }
    }

    RuniqueDialog(
        title = stringResource(id = R.string.permission_required),
        onDismiss = { /* Dismiss manually only via OK click */ },
        description = description,
        primaryButton = {
            RuniqueOutlinedActionButton(
                text = stringResource(id = R.string.okay),
                isLoading = false,
                onClick = onOkayClick
            )
        }
    )
}

/**
 * Handles the result of the permission request and dispatches relevant actions.
 *
 * @param perms Map of granted/denied permissions.
 * @param context Context used to determine if rationale should still be shown.
 * @param onAction Callback to dispatch permission-related actions.
 */
private fun handlePermissionResults(
    perms: Map<String, Boolean>,
    context: Context,
    onAction: (ActiveRunAction) -> Unit
) {
    val activity = context as ComponentActivity

    val hasCourseLocationPermission = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    val hasFineLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= 33) {
        perms[Manifest.permission.POST_NOTIFICATIONS] == true
    } else true

    onAction(
        ActiveRunAction.SubmitLocationPermissionInfo(
            acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
            showLocationRationale = activity.shouldShowLocationPermissionRationale()
        )
    )

    onAction(
        ActiveRunAction.SubmitNotificationPermissionInfo(
            acceptedNotificationPermission = hasNotificationPermission,
            showNotificationPermissionRationale = activity.shouldShowNotificationPermissionRationale()
        )
    )
}

/**
 * Initializes the permission state by checking which permissions are granted or require a rationale.
 * If no rationale is needed, automatically launches the permission request.
 *
 * @param context The current context.
 * @param onAction Callback to dispatch permission info actions.
 * @param permissionLauncher Launcher to trigger permission request dialog.
 */
private fun initializePermissionState(
    context: Context,
    onAction: (ActiveRunAction) -> Unit,
    permissionLauncher: ActivityResultLauncher<Array<String>>
) {
    val activity = context as ComponentActivity

    val showLocationRationale = activity.shouldShowLocationPermissionRationale()
    val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

    onAction(
        ActiveRunAction.SubmitLocationPermissionInfo(
            acceptedLocationPermission = context.hasLocationPermission(),
            showLocationRationale = showLocationRationale
        )
    )
    onAction(
        ActiveRunAction.SubmitNotificationPermissionInfo(
            acceptedNotificationPermission = context.hasNotificationPermission(),
            showNotificationPermissionRationale = showNotificationRationale
        )
    )

    if (!showLocationRationale && !showNotificationRationale) {
        permissionLauncher.requestRuniquePermissions(context)
    }
}

/**
 * Extension function to simplify launching location and notification permissions.
 *
 * Automatically handles Android 13+ notification permission separately.
 *
 * @param context Used to check existing permission states.
 */
private fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val notificationPermission = if(Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }
        !hasLocationPermission -> launch(locationPermissions)
        !hasNotificationPermission -> launch(notificationPermission)
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RuniqueTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onServiceToggle = {},
            onAction = {}
        )
    }
}