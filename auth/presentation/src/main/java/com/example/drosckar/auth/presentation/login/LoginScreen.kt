package com.example.drosckar.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.auth.presentation.components.EmailTextField
import com.example.drosckar.auth.presentation.components.PasswordTextField
import com.example.drosckar.core.presentation.designsystem.Poppins
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.core.presentation.designsystem.components.GradientBackground
import com.example.drosckar.core.presentation.designsystem.components.RuniqueActionButton
import com.example.drosckar.core.presentation.ui.ObserveAsEvents
import com.example.drosckar.core.presentation.ui.bringIntoViewOnFocus
import com.example.drosckar.core.presentation.ui.clearFocusOnTap
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            LoginEvent.LoginSuccess -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.youre_logged_in,
                    Toast.LENGTH_LONG
                ).show()

                onLoginSuccess()
            }
        }
    }
    LoginScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is LoginAction.OnRegisterClick -> onSignUpClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .clearFocusOnTap()
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
                .imePadding()
        ) {
            LoginHeadline()
            WelcomeText()

            Spacer(modifier = Modifier.height(48.dp))

            EmailTextField(
                modifier = Modifier.bringIntoViewOnFocus(),
                state = state.email
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                modifier = Modifier.bringIntoViewOnFocus(),
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(LoginAction.OnTogglePasswordVisibility)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            LoginButton(
                isLoading = state.isLoggingIn,
                enabled = state.canLogin && !state.isLoggingIn,
                onLoginClick = {
                    onAction(LoginAction.OnLoginClick)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterPrompt(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
            ) {
                onAction(LoginAction.OnRegisterClick)
            }
        }
    }
}

@Composable
private fun LoginHeadline() {
    Text(
        text = stringResource(id = R.string.hi_there),
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun WelcomeText() {
    Text(
        text = stringResource(id = R.string.runique_welcome_text),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun LoginButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = false,
    onLoginClick: () -> Unit
) {
    RuniqueActionButton(
        text = stringResource(id = R.string.login),
        isLoading = isLoading,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        onClick = onLoginClick
    )
}

@Composable
private fun RegisterPrompt(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = Poppins,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            append(stringResource(id = R.string.dont_have_an_account) + " ")
            withLink(
                LinkAnnotation.Clickable(
                    tag = "clickable_text",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = Poppins
                        )
                    ),
                    linkInteractionListener = LinkInteractionListener {
                        onRegisterClick()
                    }
                )
            ) {
                append(stringResource(id = R.string.sign_up))
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    RuniqueTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}