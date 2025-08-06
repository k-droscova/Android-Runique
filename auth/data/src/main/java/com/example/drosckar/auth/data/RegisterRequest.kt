package com.example.drosckar.auth.data

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing the payload for the register API call.
 *
 * This class will be serialized to JSON using Kotlinx Serialization and sent to the backend.
 *
 * @property email The email address of the user.
 * @property password The password of the user.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String
)