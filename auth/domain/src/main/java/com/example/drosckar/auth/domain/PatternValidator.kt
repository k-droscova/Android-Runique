package com.example.drosckar.auth.domain

/**
 * Defines an abstraction for pattern-based validation (e.g., email format).
 *
 * This interface allows domain-layer code to request pattern validation
 * without depending on platform-specific (e.g., Android) APIs such as `Patterns.EMAIL_ADDRESS`.
 */
interface PatternValidator {
    /**
     * Checks if the given [value] matches the pattern.
     *
     * @param value The input string to validate.
     * @return `true` if the input matches the pattern; otherwise, `false`.
     */
    fun matches(value: String): Boolean
}