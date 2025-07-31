package com.example.drosckar.auth.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drosckar.auth.domain.AuthRepository
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.auth.domain.UserDataValidator
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.presentation.ui.UiText
import com.example.drosckar.core.presentation.ui.asUiText
import com.example.drosckar.core.presentation.ui.textAsFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling login UI state and user actions.
 *
 * Coordinates validation, manages loading state, and emits UI events (e.g., login success or errors).
 *
 * @param authRepository Repository providing authentication logic (login API call).
 * @param userDataValidator Utility for validating email input.
 */
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    /**
     * Publicly exposed immutable state of the login screen.
     */
    var state by mutableStateOf(LoginState())
        private set

    /**
     * Channel used to emit one-time UI events (e.g., navigation, error messages).
     */
    private val eventChannel = Channel<LoginEvent>()
    /**
     * Flow of login events collected in the UI to react to login outcomes.
     */
    val events = eventChannel.receiveAsFlow()

    init {
        /**
         * Observe changes to email and password fields and update `canLogin` accordingly.
         *
         * Only allows login if the email is valid and password is not empty.
         * Runs every time either email or password changes.
         */
        combine(state.email.textAsFlow(), state.password.textAsFlow()) { email, password ->
            state = state.copy(
                canLogin = userDataValidator.isValidEmail(
                    email = email.toString().trim()
                ) && password.isNotEmpty()
            )
        }.launchIn(viewModelScope)
    }

    /**
     * Handles user actions from the login UI.
     *
     * @param action The user interaction to process.
     */
    fun onAction(action: LoginAction) {
        when(action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePasswordVisibility -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            else -> Unit // Other actions (e.g., navigation) may be handled in the UI layer.
        }
    }

    /**
     * Performs the login process by calling the repository and updating state/UI accordingly.
     *
     * - Shows loading indicator.
     * - Calls `authRepository.login(...)`.
     * - Emits error or success event based on the result.
     */
    private fun login() {
        viewModelScope.launch {
            // Show loading indicator
            state = state.copy(isLoggingIn = true)
            // Perform login request
            val result = authRepository.login(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString()
            )
            // Hide loading indicator
            state = state.copy(isLoggingIn = false)
            // Handle result
            when(result) {
                is Result.Error -> {
                    if(result.error == DataError.Network.UNAUTHORIZED) {
                        // Specific message for invalid credentials
                        eventChannel.send(LoginEvent.Error(
                            UiText.StringResource(R.string.error_email_password_incorrect)
                        ))
                    } else {
                        // Fallback to general network error handling
                        eventChannel.send(LoginEvent.Error(result.error.asUiText()))
                    }
                }
                is Result.Success -> {
                    // Login successful, notify UI to navigate forward
                    eventChannel.send(LoginEvent.LoginSuccess)
                }
            }
        }
    }
}