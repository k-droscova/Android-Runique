package com.example.drosckar.core.data.networking

import com.example.drosckar.core.data.BuildConfig
import com.example.drosckar.core.domain.util.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import com.example.drosckar.core.domain.util.Result

/**
 * Makes a safe GET request to the specified [route] using the Ktor [HttpClient], with optional [queryParameters].
 *
 * This function wraps the request in a [safeCall] to handle connectivity, serialization and other issues,
 * and parses the response into a [Result] with the expected [Response] type or a [DataError.Network].
 *
 * @param route Relative or full URL path (e.g., "/login" or "https://...").
 * @param queryParameters Optional map of query parameter names to values.
 * @return [Result.Success] with deserialized [Response] on success, or [Result.Error] with [DataError.Network] on failure.
 */
suspend inline fun <reified Response: Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    return safeCall {
        get {
            url(constructRoute(route))
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

/**
 * Makes a safe POST request to the specified [route] with a request [body], and deserializes the result.
 *
 * This function is designed for sending JSON-encoded payloads to a server and receiving a typed response.
 * The request is wrapped in [safeCall] for automatic error handling.
 *
 * @param route Relative or full URL path (e.g., "/runs").
 * @param body The request body to send, typically a data class.
 * @return [Result.Success] with the deserialized [Response], or [Result.Error] with [DataError.Network].
 */
suspend inline fun <reified Request, reified Response: Any> HttpClient.post(
    route: String,
    body: Request
): Result<Response, DataError.Network> {
    return safeCall {
        post {
            url(constructRoute(route))
            setBody(body)
        }
    }
}

/**
 * Makes a safe DELETE request to the given [route], with optional [queryParameters].
 *
 * DELETE requests often require identifying a resource to delete via query parameters,
 * such as an ID or token. This request is also wrapped in [safeCall] for consistent error handling.
 *
 * @param route Relative or full URL path.
 * @param queryParameters Optional query parameter map.
 * @return [Result.Success] with [Response] on success, or [Result.Error] with [DataError.Network].
 */
suspend inline fun <reified Response: Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
): Result<Response, DataError.Network> {
    return safeCall {
        delete {
            url(constructRoute(route))
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}

/**
 * Safely executes a suspendable HTTP request and wraps it in a [Result], catching common client-side exceptions.
 *
 * This protects the app from crashing due to unexpected exceptions such as:
 * - No internet connection ([UnresolvedAddressException])
 * - JSON parsing failure ([SerializationException])
 * - Unexpected unknown errors
 *
 * Cancellation exceptions are **not caught**, and are properly rethrown to allow coroutine cancellation propagation.
 *
 * @param execute A lambda returning an [HttpResponse].
 * @return [Result.Success] if the response was successful, or [Result.Error] with a mapped [DataError.Network].
 */
suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch(e: UnresolvedAddressException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Result.Error(DataError.Network.SERIALIZATION)
    } catch(e: Exception) {
        if(e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }

    return responseToResult(response)
}

/**
 * Converts an [HttpResponse] into a [Result], mapping HTTP status codes to domain-specific [DataError.Network] errors.
 *
 * Success responses (2xx) are deserialized into type [T]. Common client and server error status codes
 * (e.g., 401, 408, 429) are explicitly mapped to their corresponding error types.
 *
 * @param response The HTTP response to convert.
 * @return A [Result.Success] with parsed data, or a [Result.Error] with a domain error.
 */
suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when(response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

/**
 * Constructs a full HTTP URL based on a relative [route] and the base URL defined in [BuildConfig.BASE_URL].
 *
 * Handles three cases:
 * - If [route] already contains the full base URL → return as-is.
 * - If [route] starts with '/' → append it to the base URL.
 * - Otherwise → insert '/' between base URL and [route].
 *
 * @param route Relative or absolute URL string.
 * @return The fully qualified HTTP URL.
 */
fun constructRoute(route: String): String {
    return when {
        route.contains(BuildConfig.BASE_URL) -> route
        route.startsWith("/") -> BuildConfig.BASE_URL + route
        else -> BuildConfig.BASE_URL + "/$route"
    }
}