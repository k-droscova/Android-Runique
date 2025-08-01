package com.example.drosckar.core.presentation.designsystem.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A reusable Scaffold component with optional gradient background and custom layout slots.
 *
 * This component is a wrapper around [Scaffold] with added support for:
 * - Optional gradient background behind the content
 * - Custom [TopAppBar] and [FloatingActionButton] composables
 * - Central FAB positioning
 *
 * @param modifier Optional [Modifier] for styling the scaffold itself.
 * @param withGradient Whether the content area should include the [GradientBackground] (default is true).
 * @param topAppBar Composable lambda to display a custom top app bar (empty by default).
 * @param floatingActionButton Composable lambda to display a floating action button (empty by default).
 * @param content The main screen content, which receives [PaddingValues] from the scaffold.
 */
@Composable
fun RuniqueScaffold(
    modifier: Modifier = Modifier,
    withGradient: Boolean = true,
    topAppBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topAppBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { padding ->
        // Conditionally apply gradient background behind content
        if (withGradient) {
            GradientBackground {
                content(padding)
            }
        } else {
            content(padding)
        }
    }
}