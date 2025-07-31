package com.example.drosckar.core.data.networking

import com.example.drosckar.core.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
 * Includes standard plugins like:
 * - [ContentNegotiation] with Kotlinx Serialization
 * - [Logging] using Timber
 * - [defaultRequest] setup for common headers and content type
 */
class HttpClientFactory {

    /**
     * Builds and returns a new instance of [HttpClient] with custom plugins and configuration.
     *
     * @return A configured [HttpClient] ready for making API requests.
     */
    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                header("x-api-key", BuildConfig.API_KEY)
            }
        }
    }
}