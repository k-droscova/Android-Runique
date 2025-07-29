package com.example.drosckar.core.domain.util


/**
 * A sealed interface representing a result of an operation that can either be a [Success] or an [Error].
 *
 * This is a generic wrapper for modeling operations that may succeed with a result of type [D]
 * or fail with an error of type [E]. It avoids the need for exceptions in many use cases.
 *
 * @param D The type of successful data. Marked as [out] to allow covariance — this means that
 *          a `Result<Child, E>` can be safely used where a `Result<Parent, E>` is expected.
 * @param E The type of error, constrained to the [Error] interface. Also marked as [out] to enable
 *          similar covariance behavior..
 */
sealed interface Result<out D, out E: Error> {
    /**
     * Represents a successful result of type [D].
     *
     * @param data The data returned on success.
     */
    data class Success<out D>(val data: D) : Result<D, Nothing>
    /**
     * Represents a failure with an error of type [E].
     *
     * @param error The error object explaining the failure.
     */
    data class Error<out E: com.example.drosckar.core.domain.util.Error>(val error: E) : Result<Nothing, E>
}

/**
 * Maps the [Success] value from type [T] to type [R] using the provided [map] function.
 *
 * If the result is [Error], it is returned unchanged.
 *
 * @param map A function that transforms a value of type [T] to type [R].
 * @return A new [Result] of type [Result.Success<R>] or the original [Result.Error<E>].
 */
inline fun <T, E: Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Success -> Result.Success(map(data))
        is Result.Error -> Result.Error(error)
    }

/**
 * Converts a [Result] with data to a [Result] with [Unit] as the success value.
 *
 * Useful when the actual data is not needed and only the success/failure matters.
 *
 * @return A [Result] of type [EmptyDataResult] with [Unit] as the success value.
 */
fun <T, E: Error> Result<T, E>.asEmptyDataResult(): EmptyDataResult<E> {
    return map { }
}

/**
 * A type alias for a [Result] that represents either success with no meaningful return data (i.e. [Unit]),
 * or an error of type [E].
 */
typealias EmptyDataResult<E> = Result<Unit, E>