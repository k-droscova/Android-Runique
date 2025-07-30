package com.example.drosckar.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.auth.domain.UserDataValidator
import com.example.drosckar.core.presentation.ui.textAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel responsible for handling user registration state and validation logic.
 *
 * Observes changes to email and password text fields and updates validation state accordingly
 * using [UserDataValidator]. This ViewModel is intended for use in the registration screen.
 *
 * @param userDataValidator A domain-layer validator for checking input formats.
 */
class RegisterViewModel(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    /** Holds the current UI state of the registration screen. */
    var state by mutableStateOf(RegisterState())
        private set

    init {
        // Observe email changes and update validation state
        state.email.textAsFlow()
            .onEach { email ->
                state = state.copy(
                    isEmailValid = userDataValidator.isValidEmail(email.toString())
                )
            }
            .launchIn(viewModelScope)

        // Observe password changes and update password validation state
        state.password.textAsFlow()
            .onEach { password ->
                state = state.copy(
                    passwordValidationState = userDataValidator.validatePassword(password.toString())
                )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Handles UI actions (currently not implemented).
     *
     * @param action A [RegisterAction] triggered from the UI.
     */
    fun onAction(action: RegisterAction) {
        // TODO: Implement user actions such as register click, toggle password visibility, etc.
    }
}