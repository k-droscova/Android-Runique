package com.example.drosckar.core.domain.util


/**
 * A sealed interface grouping domain-level errors related to data operations.
 */
sealed interface DataError: Error {
    /**
     * Enum representing errors that occur during network operations.
     */
    enum class Network: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }
    /**
     * Enum representing errors that occur during local data operations.
     */
    enum class Local: DataError {
        DISK_FULL,
    }
}