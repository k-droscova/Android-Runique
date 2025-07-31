package com.example.drosckar.auth.presentation.register

import com.example.drosckar.core.presentation.ui.UiText

/**
 * UI events triggered from the [RegisterViewModel] to notify the UI of one-time effects.
 */
sealed interface RegisterEvent {

    /** Indicates that registration was successful and navigation should proceed. */
    data object RegistrationSuccess : RegisterEvent

    /**
     * Indicates that an error occurred during registration.
     *
     * @property error A [UiText] message that can be displayed in a toast or snackbar.
     */
    data class Error(val error: UiText) : RegisterEvent
}