package com.example.drosckar.auth.presentation.register

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drosckar.auth.domain.PasswordValidationState
import com.example.drosckar.auth.domain.UserDataValidator
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.core.presentation.designsystem.CheckIcon
import com.example.drosckar.core.presentation.designsystem.CrossIcon
import com.example.drosckar.core.presentation.designsystem.EmailIcon
import com.example.drosckar.core.presentation.designsystem.Poppins
import com.example.drosckar.core.presentation.designsystem.RuniqueDarkRed
import com.example.drosckar.core.presentation.designsystem.RuniqueGray
import com.example.drosckar.core.presentation.designsystem.RuniqueGreen
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.core.presentation.designsystem.components.GradientBackground
import com.example.drosckar.core.presentation.designsystem.components.RuniqueActionButton
import com.example.drosckar.core.presentation.designsystem.components.RuniquePasswordTextField
import com.example.drosckar.core.presentation.designsystem.components.RuniqueTextField
import com.example.drosckar.core.presentation.ui.bringIntoViewOnFocus
import org.koin.androidx.compose.koinViewModel


@Composable
fun RegisterScreenRoot(
    onSignInClick: () -> Unit,
    onSuccessfulRegistration: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel(),
) {
    RegisterScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    GradientBackground {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
                .imePadding()
        ) {
            Headline()
            LoginPrompt(
                onLoginClick = {
                    onAction(RegisterAction.OnLoginClick)
                }
            )
            Spacer(modifier = Modifier.height(48.dp))
            EmailTextField(
                modifier = Modifier.bringIntoViewOnFocus(),
                state = state.email,
                isEmailValid = state.isEmailValid,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordTextField(
                modifier = Modifier.bringIntoViewOnFocus(),
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordRequirements(
                validationState = state.passwordValidationState
            )
            Spacer(modifier = Modifier.height(32.dp))
            RegisterButton(
                isLoading = state.isRegistering,
                enabled = state.canRegister,
                onRegisterClick = {
                    onAction(RegisterAction.OnRegisterClick)
                }
            )
        }
    }
}

@Composable
private fun Headline(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.create_account),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
private fun LoginPrompt(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontFamily = Poppins,
                color = RuniqueGray
            )
        ) {
            append(stringResource(id = R.string.already_have_an_account) + " ")
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
                        onLoginClick
                    }
                )
            ) {
                append(stringResource(id = R.string.login))
            }
        }
    }
    Text(
        modifier = modifier,
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun EmailTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState(),
    isEmailValid: Boolean = false,
) {
    RuniqueTextField(
        state = state,
        startIcon = EmailIcon,
        endIcon = if (isEmailValid) {
            CheckIcon
        } else null,
        hint = stringResource(id = R.string.example_email),
        title = stringResource(id = R.string.email),
        modifier = modifier.fillMaxWidth(),
        additionalInfo = stringResource(id = R.string.must_be_a_valid_email),
        keyboardType = KeyboardType.Email
    )
}

@Composable
private fun PasswordTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = TextFieldState(),
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {},
) {
    RuniquePasswordTextField(
        state = state,
        isPasswordVisible = isPasswordVisible,
        onTogglePasswordVisibility = onTogglePasswordVisibility,
        hint = stringResource(id = R.string.password),
        title = stringResource(id = R.string.password),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun PasswordRequirements(
    validationState: PasswordValidationState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PasswordRequirement(
            text = stringResource(
                id = R.string.at_least_x_characters,
                UserDataValidator.MIN_PASSWORD_LENGTH
            ),
            isValid = validationState.hasMinLength
        )
        Spacer(modifier = Modifier.height(4.dp))

        PasswordRequirement(
            text = stringResource(id = R.string.at_least_one_number),
            isValid = validationState.hasNumber
        )
        Spacer(modifier = Modifier.height(4.dp))

        PasswordRequirement(
            text = stringResource(id = R.string.contains_lowercase_char),
            isValid = validationState.hasLowerCaseCharacter
        )
        Spacer(modifier = Modifier.height(4.dp))

        PasswordRequirement(
            text = stringResource(id = R.string.contains_uppercase_char),
            isValid = validationState.hasUpperCaseCharacter
        )
    }
}

@Composable
private fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) {
                CheckIcon
            } else {
                CrossIcon
            },
            contentDescription = null,
            tint = if(isValid) RuniqueGreen else RuniqueDarkRed
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun RegisterButton(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = false,
    onRegisterClick: () -> Unit
) {
    RuniqueActionButton(
        text = stringResource(id = R.string.register),
        isLoading = isLoading,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        onClick = onRegisterClick
    )
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    RuniqueTheme {
        RegisterScreen(
            state = RegisterState(
                passwordValidationState = PasswordValidationState(
                    hasNumber = true,
                )
            ),
            onAction = {}
        )
    }
}