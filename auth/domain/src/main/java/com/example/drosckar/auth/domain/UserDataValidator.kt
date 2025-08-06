package com.example.drosckar.auth.domain

/**
 * Encapsulates logic for validating user input like email addresses and passwords.
 *
 * Belongs in the domain layer, as it contains business rules and logic
 * without depending on platform-specific code.
 *
 * @param patternValidator An implementation of [PatternValidator] to check email format.
 */
class UserDataValidator(
    private val patternValidator: PatternValidator
) {

    /**
     * Checks if the given [email] is valid using the injected [PatternValidator].
     *
     * Trims the email before validating.
     *
     * @param email The input email string to validate.
     * @return `true` if the email matches a valid format.
     */
    fun isValidEmail(email: String): Boolean {
        return patternValidator.matches(email.trim())
    }

    /**
     * Validates the [password] against several common password rules:
     * - Minimum length
     * - At least one digit
     * - At least one lowercase letter
     * - At least one uppercase letter
     *
     * Returns a [PasswordValidationState] object that indicates which criteria are met.
     *
     * @param password The input password string.
     * @return [PasswordValidationState] representing the validation result.
     */
    fun validatePassword(password: String): PasswordValidationState {
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasDigit = password.any { it.isDigit() }
        val hasLowerCaseCharacter = password.any { it.isLowerCase() }
        val hasUpperCaseCharacter = password.any { it.isUpperCase() }

        return PasswordValidationState(
            hasMinLength = hasMinLength,
            hasNumber = hasDigit,
            hasLowerCaseCharacter = hasLowerCaseCharacter,
            hasUpperCaseCharacter = hasUpperCaseCharacter
        )
    }

    companion object {
        /** Minimum required length for a valid password. */
        const val MIN_PASSWORD_LENGTH = 9
    }
}