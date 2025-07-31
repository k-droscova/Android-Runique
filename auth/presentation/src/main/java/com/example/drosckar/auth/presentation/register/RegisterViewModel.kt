package com.example.drosckar.auth.presentation.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.auth.domain.AuthRepository
import com.example.drosckar.auth.domain.UserDataValidator
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.presentation.ui.textAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.presentation.ui.UiText
import com.example.drosckar.core.presentation.ui.asUiText

/**
 * ViewModel responsible for handling user registration state, validation, and interaction with the backend.
 *
 * This ViewModel manages the registration form inputs, validates them using [UserDataValidator],
 * and handles the API call to register a new user via [AuthRepository].
 *
 * @param userDataValidator Validates the email and password formats according to app rules.
 * @param repository Provides authentication operations like registration.
 */
class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val repository: AuthRepository
) : ViewModel() {

    /** Holds the current UI state of the registration screen. */
    var state by mutableStateOf(RegisterState())
        private set

    /** Channel for one-time UI events (e.g. toast messages, navigation). */
    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

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
     * Handles user actions from the UI.
     *
     * @param action The [RegisterAction] representing the user's interaction.
     */
    fun onAction(action: RegisterAction) {
        when(action) {
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            else -> Unit
        }
    }

    /**
     * Attempts to register a new user using the current input values.
     *
     * Handles success and failure cases by updating UI state and emitting events.
     */
    private fun register() {
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            val result = repository.register(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString()
            )
            state = state.copy(isRegistering = false)

            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.CONFLICT) {
                        eventChannel.send(RegisterEvent.Error(
                            UiText.StringResource(R.string.error_email_exists)
                        ))
                    } else {
                        eventChannel.send(RegisterEvent.Error(result.error.asUiText()))
                    }
                }
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
            }
        }
    }
}