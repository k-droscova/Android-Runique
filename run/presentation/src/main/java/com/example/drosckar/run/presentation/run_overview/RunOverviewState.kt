package com.example.drosckar.run.presentation.run_overview

import com.example.drosckar.run.presentation.run_overview.model.RunUi

/**
 * Holds the UI state for the Run Overview screen.
 *
 * This state is observed by the UI to display the user's past runs.
 *
 * @property runs The list of previously recorded runs, formatted for display using the [RunUi] model.
 *                This list is updated in real-time based on the local database and remote syncs.
 */
data class RunOverviewState(
    val runs: List<RunUi> = emptyList()
)