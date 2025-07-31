package com.example.drosckar.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.core.presentation.designsystem.CheckIcon
import com.example.drosckar.core.presentation.designsystem.EmailIcon
import com.example.drosckar.core.presentation.designsystem.components.RuniqueTextField

@Composable
fun EmailTextField(
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