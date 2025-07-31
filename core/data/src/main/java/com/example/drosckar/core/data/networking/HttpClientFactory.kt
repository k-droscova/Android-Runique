package com.example.drosckar.core.data.networking

import com.example.drosckar.core.data.BuildConfig
import com.example.drosckar.core.domain.util.AuthInfo
import com.example.drosckar.core.domain.util.SessionStorage
import com.example.drosckar.core.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * Factory responsible for building a configured instance of [HttpClient] using the CIO engine.
 *
 * This client includes:
 * - [ContentNegotiation] with Kotlinx Serialization for JSON
 * - [Logging] using Timber (level = ALL)
 * - [defaultRequest] with `Content-Type` and API key header
 * - [Auth] plugin for Bearer token-based authentication
 *   - Automatically loads stored tokens
 *   - Automatically refreshes access tokens on 401 Unauthorized responses
 *
 * @param sessionStorage Storage mechanism for managing access/refresh tokens locally.
 */
class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {

    /**
     * Builds and returns a new instance of [HttpClient] with standard plugins and token refresh logic.
     *
     * @return A fully configured [HttpClient] ready for authenticated API communication.
     */
    fun build(): HttpClient {
        return HttpClient(CIO) {

            // Enables automatic (de)serialization of JSON payloads using kotlinx.serialization
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true // Ignore unknown fields in server responses
                    }
                )
            }

            // Logs all HTTP activity using Timber for debugging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }

            // Sets up default headers for every request (e.g., Content-Type, API key)
            defaultRequest {
                contentType(ContentType.Application.Json)
                header("x-api-key", BuildConfig.API_KEY)
            }

            // Handles Bearer token-based authentication and automatic refresh
            install(Auth) {
                bearer {

                    /**
                     * Called before each request to attach current access/refresh tokens.
                     * If access token is invalid, server will respond with 401 → triggers refresh.
                     */
                    loadTokens {
                        val info = sessionStorage.get()
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: ""
                        )
                    }

                    /**
                     * Called after a 401 Unauthorized response.
                     * Makes a request to /accessToken with the refresh token to obtain a new access token.
                     */
                    refreshTokens {
                        val info = sessionStorage.get()

                        // Attempt to request a new access token from the server
                        val response = client.post<AccessTokenRequest, AccessTokenResponse>(
                            route = "/accessToken",
                            body = AccessTokenRequest(
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                        )

                        return@refreshTokens if (response is Result.Success) {
                            // Save the new access token while keeping the existing refresh token
                            val newAuthInfo = AuthInfo(
                                accessToken = response.data.accessToken,
                                refreshToken = info?.refreshToken ?: "",
                                userId = info?.userId ?: ""
                            )
                            sessionStorage.set(newAuthInfo)

                            // Return updated bearer tokens so Ktor can retry the failed request
                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        } else {
                            // Refresh failed (e.g., invalid refresh token), return empty tokens
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }
            }
        }
    }
}