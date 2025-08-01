package com.example.drosckar.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.drosckar.core.presentation.designsystem.RunIcon
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme

/**
 * A custom floating action button used across the Runique app.
 *
 * This FAB has a two-layer circular design:
 * - An outer, semi-transparent primary-colored circle (75dp)
 * - An inner, solid primary-colored circle (50dp) with a centered icon
 *
 * @param icon The [ImageVector] to be shown inside the button.
 * @param onClick The callback to be triggered when the FAB is clicked.
 * @param modifier Optional [Modifier] for styling and positioning.
 * @param contentDescription Optional content description for accessibility.
 * @param iconSize The size of the icon inside the button (default: 25dp).
 */
@Composable
fun RuniqueFloatingActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconSize: Dp = 25.dp
) {
    // Outer circle - semi-transparent with clickable behavior
    Box(
        modifier = modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Inner circle - solid background containing the icon
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(12.dp), // Padding around the icon
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}


@Preview
@Composable
private fun RuniqueFloatingButtonPreview() {
    RuniqueTheme {
        RuniqueFloatingActionButton(
            icon = RunIcon,
            onClick = { /*TODO*/ }
        )
    }
}