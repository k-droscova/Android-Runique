package com.example.drosckar.core.presentation.ui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow

/**
 * Converts a [TextFieldState]'s text value into a [Flow] that emits updates on change.
 *
 * This is useful for observing user input in a reactive way (e.g., for validation).
 */
fun TextFieldState.textAsFlow() = snapshotFlow { text }