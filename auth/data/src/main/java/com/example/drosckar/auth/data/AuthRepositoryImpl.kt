package com.example.drosckar.auth.data

import com.example.drosckar.auth.domain.AuthRepository
import com.example.drosckar.core.data.networking.post
import com.example.drosckar.core.domain.util.AuthInfo
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.SessionStorage
import com.example.drosckar.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

/**
 * Implementation of [AuthRepository] that communicates with a remote API using Ktor.
 *
 * @property httpClient An instance of [HttpClient] used for making HTTP requests.
 */
class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
) : AuthRepository {

    /**
     * Sends a POST request to the `/register` endpoint with the given user credentials.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @return [EmptyResult] indicating success or a network-related error.
     */
    override suspend fun register(email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(email, password)
        )
    }
    /**
     * Logs in the user by making a POST request to the backend.
     * If successful, stores the access and refresh tokens in [SessionStorage].
     *
     * @return A [Result] with [Unit] on success, or a [DataError.Network] on failure.
     */
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if(result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }
        return result.asEmptyDataResult()
    }
}