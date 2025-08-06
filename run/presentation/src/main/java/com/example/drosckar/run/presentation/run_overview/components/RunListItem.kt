@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.example.drosckar.run.presentation.run_overview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.drosckar.core.domain.location.Location
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.presentation.designsystem.CalendarIcon
import com.example.drosckar.core.presentation.designsystem.RunOutlinedIcon
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.run.presentation.R
import com.example.drosckar.run.presentation.run_overview.mapper.toRunUi
import com.example.drosckar.run.presentation.run_overview.model.RunDataUi
import com.example.drosckar.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.math.max
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * A list item representing a single run entry.
 * Displays a map preview, key run statistics, and supports a long-click delete menu.
 *
 * @param runUi The run data to display (already formatted).
 * @param onDeleteClick Lambda triggered when the user selects "Delete" from the dropdown menu.
 * @param modifier Modifier for layout customizations.
 */
@Composable
fun RunListItem(
    runUi: RunUi,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    Box {
        // Main content area of the card
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(15.dp)) // Round corners
                .background(MaterialTheme.colorScheme.surface) // Card background
                .combinedClickable(
                    onClick = {}, // No normal click action
                    onLongClick = { showDropDown = true } // Show menu on long click
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MapImage(imageUrl = runUi.mapPictureUrl)
            RunningTimeSection(
                duration = runUi.duration,
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            RunningDateSection(dateTime = runUi.dateTime)
            DataGrid(
                run = runUi,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Dropdown menu for deleting the run
        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.delete))
                },
                onClick = {
                    showDropDown = false
                    onDeleteClick()
                }
            )
        }
    }
}

/**
 * Displays the static map image preview of the run using Coil.
 *
 * @param imageUrl URL of the map image.
 * @param modifier Modifier for layout control.
 */
@Composable
private fun MapImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = stringResource(id = R.string.run_map),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
            .clip(RoundedCornerShape(15.dp)),
        loading = {
            // Show a circular loader while the image loads
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        error = {
            // Show fallback UI if image loading fails
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.error_couldnt_load_image),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

/**
 * Displays the duration of the run with a run icon.
 *
 * @param duration Formatted duration string (e.g., "10:32").
 * @param modifier Modifier for layout control.
 */
@Composable
private fun RunningTimeSection(
    duration: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunOutlinedIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        // Labels
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.total_running_time),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = duration,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Displays the date the run was recorded, along with a calendar icon.
 *
 * @param dateTime Formatted date string.
 * @param modifier Modifier for layout control.
 */
@Composable
private fun RunningDateSection(
    dateTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = CalendarIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = dateTime,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Displays a responsive grid layout with all key run metrics.
 *
 * Ensures that all cells have equal width by calculating the maximum cell width dynamically.
 *
 * @param run The run UI data to visualize.
 * @param modifier Modifier for layout control.
 */
@Composable
private fun DataGrid(
    run: RunUi,
    modifier: Modifier = Modifier
) {
    val runDataUiList = listOf(
        RunDataUi(stringResource(id = R.string.distance), run.distance),
        RunDataUi(stringResource(id = R.string.pace), run.pace),
        RunDataUi(stringResource(id = R.string.avg_speed), run.avgSpeed),
        RunDataUi(stringResource(id = R.string.max_speed), run.maxSpeed),
        RunDataUi(stringResource(id = R.string.total_elevation), run.totalElevation)
    )

    // Track the widest cell for consistent layout
    var maxWidth by remember { mutableIntStateOf(0) }
    val maxWidthDp = with(LocalDensity.current) { maxWidth.toDp() }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        runDataUiList.forEach { runData ->
            DataGridCell(
                runData = runData,
                modifier = Modifier
                    .defaultMinSize(minWidth = maxWidthDp)
                    .onSizeChanged { size ->
                        maxWidth = max(maxWidth, size.width)
                    }
            )
        }
    }
}

/**
 * Represents a single cell in the metrics grid.
 * Displays a label and a formatted value (e.g., "Distance", "3.5 km").
 *
 * @param runData The metric data to display.
 * @param modifier Modifier for layout control.
 */
@Composable
private fun DataGridCell(
    runData: RunDataUi,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = runData.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = runData.value,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    RuniqueTheme {
        RunListItem(
            runUi = Run(
                id = "123",
                duration = 10.minutes + 30.seconds,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 2543,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 15.6234,
                totalElevationMeters = 123,
                mapPictureUrl = null
            ).toRunUi(),
            onDeleteClick = {}
        )
    }
}