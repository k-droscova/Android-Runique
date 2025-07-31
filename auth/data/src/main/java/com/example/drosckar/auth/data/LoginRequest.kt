package com.example.drosckar.auth.data

import kotlinx.serialization.Serializable

/**
 * Represents the data sent to the backend when a user attempts to log in.
 *
 * @property email The email address entered by the user.
 * @property password The password entered by the user.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)