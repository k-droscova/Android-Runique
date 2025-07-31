package com.example.drosckar.auth.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.drosckar.auth.presentation.R
import com.example.drosckar.core.presentation.designsystem.components.RuniquePasswordTextField

@Composable
fun PasswordTextField(
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