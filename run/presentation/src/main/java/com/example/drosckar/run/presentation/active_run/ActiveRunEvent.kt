package com.example.drosckar.run.presentation.active_run

import com.example.drosckar.core.presentation.ui.UiText

/**
 * One-time UI events emitted by the Active Run ViewModel.
 */
sealed interface ActiveRunEvent {
    /** Run was saved successfully. */
    data object RunSaved: ActiveRunEvent

    /** An error occurred (e.g. during save). */
    data class Error(val error: UiText): ActiveRunEvent
}