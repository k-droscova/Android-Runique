package com.example.drosckar.auth.domain

import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult

/**
 * Repository interface for authentication-related operations.
 *
 * Provides an abstraction over different data sources (e.g., network or local storage) to
 * perform user authentication tasks such as registering or logging in.
 */
interface AuthRepository {

    /**
     * Registers a new user with the given email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return [EmptyResult] indicating success or a specific [DataError.Network] failure.
     */
    suspend fun register(email: String, password: String): EmptyResult<DataError.Network>
    /**
     * Attempts to log in the user with the given credentials.
     *
     * Stores tokens and user info in session storage on success.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Result] indicating success or failure.
     */
    suspend fun login(email: String, password: String): EmptyResult<DataError.Network>
}