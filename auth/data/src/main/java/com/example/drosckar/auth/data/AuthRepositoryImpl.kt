package com.example.drosckar.auth.data

import com.example.drosckar.auth.domain.AuthRepository
import com.example.drosckar.core.data.networking.post
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.DataError
import io.ktor.client.HttpClient

/**
 * Implementation of [AuthRepository] that communicates with a remote API using Ktor.
 *
 * @property httpClient An instance of [HttpClient] used for making HTTP requests.
 */
class AuthRepositoryImpl(
    private val httpClient: HttpClient
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
}