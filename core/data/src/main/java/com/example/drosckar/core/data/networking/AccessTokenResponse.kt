package com.example.drosckar.core.data.networking

import kotlinx.serialization.Serializable

/**
 * Response payload received from the server after refreshing the access token.
 *
 * @property accessToken The new short-lived access token.
 * @property expirationTimestamp When the token will expire (server-defined format).
 */
@Serializable
data class AccessTokenResponse(
    val accessToken: String,
    val expirationTimestamp: Long
)