package com.example.drosckar.auth.data

import android.util.Patterns
import com.example.drosckar.auth.domain.PatternValidator

/**
 * A platform-specific implementation of [PatternValidator] for validating email addresses.
 *
 * Uses Android's built-in `Patterns.EMAIL_ADDRESS` to check validity.
 * Should only be used in modules that have access to the Android SDK (e.g., data layer).
 */
object EmailPatternValidator : PatternValidator {
    override fun matches(value: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(value).matches()
    }
}