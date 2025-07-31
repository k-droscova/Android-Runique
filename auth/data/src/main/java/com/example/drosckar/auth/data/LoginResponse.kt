package com.example.drosckar.auth.data

import kotlinx.serialization.Serializable

/**
 * Represents the server response after a successful login request.
 *
 * @property accessToken The short-lived token used for authenticated requests.
 * @property refreshToken The long-lived token used to request new access tokens.
 * @property accessTokenExpirationTimestamp The expiration timestamp of the access token (in epoch milliseconds).
 * @property userId The unique identifier of the authenticated user.
 */
@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpirationTimestamp: Long,
    val userId: String
)