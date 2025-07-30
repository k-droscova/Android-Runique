package com.example.drosckar.auth.domain

/**
 * Represents the result of validating a password against multiple criteria.
 *
 * Used to provide feedback to the user about which password requirements are fulfilled.
 *
 * @property hasMinLength Whether the password meets the minimum length requirement.
 * @property hasNumber Whether the password contains at least one numeric digit.
 * @property hasLowerCaseCharacter Whether the password contains at least one lowercase letter.
 * @property hasUpperCaseCharacter Whether the password contains at least one uppercase letter.
 */
data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasNumber: Boolean = false,
    val hasLowerCaseCharacter: Boolean = false,
    val hasUpperCaseCharacter: Boolean = false
) {
    /**
     * Returns `true` if all password validation rules are satisfied.
     */
    val isValidPassword: Boolean
        get() = hasMinLength && hasNumber && hasLowerCaseCharacter && hasUpperCaseCharacter
}