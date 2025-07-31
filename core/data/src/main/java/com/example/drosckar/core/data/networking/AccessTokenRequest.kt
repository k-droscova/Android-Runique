package com.example.drosckar.core.data.networking

import kotlinx.serialization.Serializable

/**
 * Request payload sent to the server when refreshing an access token.
 *
 * @property refreshToken The long-lived refresh token used to authenticate the user.
 * @property userId The user's unique identifier.
 */
@Serializable
data class AccessTokenRequest(
    val refreshToken: String,
    val userId: String
)