package com.example.drosckar.run.network

import com.example.drosckar.core.data.networking.constructRoute
import com.example.drosckar.core.data.networking.delete
import com.example.drosckar.core.data.networking.get
import com.example.drosckar.core.data.networking.safeCall
import com.example.drosckar.core.domain.run.RemoteRunDataSource
import com.example.drosckar.core.domain.run.Run
import com.example.drosckar.core.domain.util.DataError
import com.example.drosckar.core.domain.util.EmptyResult
import com.example.drosckar.core.domain.util.Result
import com.example.drosckar.core.domain.util.map
import com.example.drosckar.run.network.dto.RunDto
import com.example.drosckar.run.network.mappers.toCreateRunRequest
import com.example.drosckar.run.network.mappers.toRun
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json


/**
 * Ktor-based implementation of [RemoteRunDataSource] using a provided [HttpClient].
 *
 * Handles network communication with the backend API for run-related operations.
 */
class KtorRemoteRunDataSource(
    private val httpClient: HttpClient
) : RemoteRunDataSource {

    /**
     * Fetches a list of runs from the backend API.
     *
     * Calls GET `/runs` and maps the returned [RunDto] list to [Run] domain models.
     */
    override suspend fun getRuns(): Result<List<Run>, DataError.Network> {
        return httpClient.get<List<RunDto>>(
            route = "/runs"
        ).map { runDtos ->
            runDtos.map { it.toRun() }
        }
    }

    /**
     * Uploads a new run and map image via a multipart POST request.
     *
     * Sends:
     * - JSON stringified [CreateRunRequest] under "RUN_DATA" key.
     * - JPEG image as "MAP_PICTURE".
     *
     * Expects a [RunDto] in response, which is mapped back to a [Run].
     */
    override suspend fun postRun(run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        val createRunRequestJson = Json.encodeToString(run.toCreateRunRequest())

        val result = safeCall<RunDto> {
            httpClient.submitFormWithBinaryData(
                url = constructRoute("/run"),
                formData = formData {
                    // Attach map picture file part
                    append("MAP_PICTURE", mapPicture, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=mappicture.jpg")
                    })

                    // Attach run metadata as JSON string
                    append("RUN_DATA", createRunRequestJson, Headers.build {
                        append(HttpHeaders.ContentType, "text/plain")
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"RUN_DATA\"")
                    })
                }
            ) {
                method = HttpMethod.Post
            }
        }

        return result.map { it.toRun() }
    }

    /**
     * Deletes a run on the backend by ID via a DELETE request.
     *
     * Calls DELETE `/run?id=<id>`.
     */
    override suspend fun deleteRun(id: String): EmptyResult<DataError.Network> {
        return httpClient.delete(
            route = "/run",
            queryParameters = mapOf("id" to id)
        )
    }
}