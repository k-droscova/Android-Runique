package com.example.drosckar.core.domain.run

import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.Result

/**
 * Remote data source interface for managing run data via a backend API.
 *
 * This acts as the counterpart to a local data source, providing a unified abstraction
 * to fetch, post, and delete run entries from the server.
 */
interface RemoteRunDataSource {

    /**
     * Fetches all runs associated with the user from the backend.
     *
     * @return A [Result] containing either a list of [Run] domain models or a [DataError.Network] on failure.
     */
    suspend fun getRuns(): Result<List<Run>, DataError.Network>

    /**
     * Uploads a new run and its associated map picture to the backend.
     *
     * This sends a multipart request with both a JSON body (run metadata) and a JPEG image.
     *
     * @param run The run domain model to upload.
     * @param mapPicture The JPEG image (as [ByteArray]) representing the run's map.
     * @return A [Result] containing the saved [Run] returned by the backend, or a [DataError.Network] on failure.
     */
    suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network>

    /**
     * Deletes a specific run from the backend using its ID.
     *
     * @param id The unique ID of the run to delete.
     * @return An [EmptyResult] indicating success or a [DataError.Network] on failure.
     */
    suspend fun deleteRun(id: String): EmptyResult<DataError.Network>
}