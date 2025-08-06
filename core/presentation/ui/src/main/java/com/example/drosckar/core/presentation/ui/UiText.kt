package com.example.drosckar.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * A sealed interface representing UI text that can either be a raw string or a string resource.
 *
 * This abstraction helps decouple your ViewModel or domain layer from Android's [Context],
 * allowing UI text to be passed around safely and localized when needed.
 */
sealed interface UiText {

    /**
     * Represents a raw string that is already fully formatted and ready for display.
     *
     * @property value The string to display.
     */
    data class DynamicString(val value: String) : UiText

    /**
     * Represents a reference to a string resource, optionally with formatting arguments.
     *
     * This should be used for all user-facing strings that need localization.
     *
     * @property resId The string resource ID (e.g. `R.string.error_unknown`).
     * @property args Optional arguments to format the string (e.g. names, numbers).
     */
    class StringResource(
        @field:StringRes val resId: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText

    /**
     * Resolves the [UiText] into a [String] within a Composable context.
     *
     * This method should be used from inside a `@Composable` function.
     *
     * @return The resolved string, either directly from [DynamicString] or via [stringResource] for [StringResource].
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    /**
     * Resolves the [UiText] into a [String] using a given [Context].
     *
     * This is useful outside of a Composable.
     *
     * @param context The Android context used to access resources.
     * @return The resolved string.
     */
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}