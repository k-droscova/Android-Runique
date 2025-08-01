package com.example.drosckar.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drosckar.core.presentation.designsystem.RuniqueTheme
import com.example.drosckar.core.presentation.ui.formatted
import com.example.drosckar.core.presentation.ui.toFormattedKm
import com.example.drosckar.core.presentation.ui.toFormattedPace
import com.example.drosckar.run.domain.RunData
import com.example.drosckar.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Card displaying the current run's duration, distance, and average pace.
 *
 * @param elapsedTime Duration of the current run.
 * @param runData Data containing distance and route history.
 */
@Composable
fun RunDataCard(
    elapsedTime: Duration,
    runData: RunData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunDataItem(
            title = stringResource(id = R.string.duration),
            value = elapsedTime.formatted(),
            valueFontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RunDataItem(
                title = stringResource(id = R.string.distance),
                value = (runData.distanceMeters / 1000.0).toFormattedKm(),
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp)
            )
            RunDataItem(
                title = stringResource(id = R.string.pace),
                value = elapsedTime.toFormattedPace(
                    distanceKm = (runData.distanceMeters / 1000.0)
                ),
                modifier = Modifier
                    .defaultMinSize(minWidth = 75.dp)
            )
        }
    }
}

/**
 * UI element for displaying a single labeled run data value.
 */
@Composable
private fun RunDataItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueFontSize: TextUnit = 16.sp
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = valueFontSize
        )
    }
}

@Preview
@Composable
private fun RunDataCardPreview() {
    RuniqueTheme {
        RunDataCard(
            elapsedTime = 20.minutes,
            runData = RunData(
                distanceMeters = 3425,
                pace = 3.minutes
            )
        )
    }
}