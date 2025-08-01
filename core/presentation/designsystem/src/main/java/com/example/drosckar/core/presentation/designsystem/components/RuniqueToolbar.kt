@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.drosckar.core.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.drosckar.core.presentation.designsystem.AnalyticsIcon
import com.example.drosckar.core.presentation.designsystem.ArrowLeftIcon
import com.example.drosckar.core.presentation.designsystem.LogoIcon
import com.example.drosckar.core.presentation.designsystem.Poppins
import com.example.drosckar.core.presentation.designsystem.R
import com.example.drosckar.core.presentation.designsystem.RuniqueGreen
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.core.presentation.designsystem.components.util.DropDownItem

/**
 * A customizable top app bar for the Runique app.
 *
 * Supports an optional back button, a title, a start content slot, and an overflow menu (dropdown).
 * Can be used with scroll behavior to respond to nested scroll changes (e.g., collapsing effects).
 *
 * @param showBackButton Whether to show the navigation (back) button on the left.
 * @param title The title displayed in the center of the app bar.
 * @param modifier Optional [Modifier] to style the app bar.
 * @param menuItems Optional list of [DropDownItem]s to display in a dropdown menu.
 * @param onMenuItemClick Callback when a dropdown menu item is selected, returns index.
 * @param onBackClick Callback when the back button is pressed.
 * @param scrollBehavior Scroll behavior passed down to [TopAppBar] (e.g. for collapse on scroll).
 * @param startContent Optional slot to inject custom content at the start of the title row (e.g. icon or avatar).
 */
@Composable
fun RuniqueToolbar(
    showBackButton: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    menuItems: List<DropDownItem> = emptyList(),
    onMenuItemClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    startContent: (@Composable () -> Unit)? = null
) {
    // Track dropdown menu visibility
    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Optional composable content to the left of the title (e.g. icon)
                startContent?.invoke()
                Spacer(modifier = Modifier.width(8.dp))
                // Main title text
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = Poppins
                )
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent // Transparent background for gradient or image
        ),
        navigationIcon = {
            if(showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ArrowLeftIcon,
                        contentDescription = stringResource(id = R.string.go_back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            // If there are menu items, show overflow menu button and dropdown
            if(menuItems.isNotEmpty()) {
                Box {
                    DropdownMenu(
                        expanded = isDropDownOpen,
                        onDismissRequest = {
                            isDropDownOpen = false
                        }
                    ) {
                        menuItems.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onMenuItemClick(index) }
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item.title)
                            }
                        }
                    }
                    // Icon to open the dropdown menu
                    IconButton(onClick = {
                        isDropDownOpen = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.open_menu),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun RuniqueToolbarPreview() {
    RuniqueTheme {
        RuniqueToolbar(
            showBackButton = false,
            title = "Runique",
            modifier = Modifier.fillMaxWidth(),
            startContent = {
                Icon(
                    imageVector = LogoIcon,
                    contentDescription = null,
                    tint = RuniqueGreen,
                    modifier = Modifier
                        .size(35.dp)
                )
            },
            menuItems = listOf(
                DropDownItem(
                    icon = AnalyticsIcon,
                    title = "Analytics"
                )
            )
        )
    }
}