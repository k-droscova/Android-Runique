package com.example.drosckar.core.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * A utility [Composable] function for collecting one-time events from a [Flow] in a lifecycle-aware manner.
 *
 * One-time events refer to actions that should occur only once — for example:
 * - Showing a toast or snackbar
 * - Navigating to another screen after login
 * - Displaying dialogs
 *
 * These events are usually emitted by the ViewModel and **should not be re-triggered** during configuration changes
 * (like screen rotation), unlike persistent UI state. To avoid event loss or accidental re-execution,
 * this function leverages [repeatOnLifecycle] and collects only when the lifecycle is at least in [Lifecycle.State.STARTED].
 *
 * Internally, this uses [LaunchedEffect] and collects the flow on [Dispatchers.Main.immediate] to ensure minimal delay
 * between UI state changes and event handling, avoiding edge cases where events could otherwise be lost
 * if emitted while the lifecycle is in a non-active state (e.g., `DESTROYED` during rotation).
 *
 * @param flow The [Flow] of one-time events to observe.
 * @param key1 An optional key that will trigger re-execution of the [LaunchedEffect] when it changes.
 * @param key2 An optional second key for additional recomposition triggers.
 * @param onEvent A lambda to handle each emitted event from the [Flow].
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}