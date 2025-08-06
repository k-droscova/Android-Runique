package com.example.drosckar.auth.presentation.register

import androidx.compose.foundation.text.input.TextFieldState
import com.example.drosckar.auth.domain.PasswordValidationState

/**
* Holds the UI state for the user registration screen.
*
* Combines email and password fields, their validation states, and loading status for the registration process.
*
* @property email The current value and state of the email input field.
* @property isEmailValid Whether the entered email is currently considered valid.
* @property password The current value and state of the password input field.
* @property isPasswordVisible Whether the password should be visible (e.g., toggled via an eye icon).
* @property passwordValidationState Contains flags for which password rules are currently satisfied.
* @property isRegistering Whether the app is currently performing the registration request.
*/
data class RegisterState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isRegistering: Boolean = false,
) {
    /**
     * Whether the user is allowed to submit the registration form.
     *
     * This is a computed property and is not included in `copy()` operations.
     * It becomes `true` only when the password is valid and registration is not in progress.
     */
    val canRegister: Boolean
        get() = passwordValidationState.isValidPassword && !isRegistering
}