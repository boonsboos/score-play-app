package nl.connectplay.scoreplay.api.http

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import nl.connectplay.scoreplay.constants.Constants

data class ApiResult<T>(val response: HttpResponse, val data: T?)

class ApiClient(
    private val baseUrl: String = Constants.BASE_URL,
    private val accessTokenProvider: () -> String? = { Constants.ACCESS_TOKEN },
    private val defaultTimeoutMs: Long = Constants.MINUTE_MS
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) { requestTimeoutMillis = defaultTimeoutMs }
        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            accessTokenProvider()?.let { token ->
                if (token.isNotBlank()) header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        expectSuccess = false // we’ll raise HttpError ourselves
    }

    /**
     * `request(path, init)`.
     * - Cancels via per-call timeout (like AbortController).
     * - Parses JSON when content-type contains application/json and status != 204.
     */
    suspend inline fun <reified T> request(
        path: String,
        method: HttpMethod = HttpMethod.Get,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): ApiResult<T> {
        val job = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + job)

        try {
            val response = scope.async {
                client.request(path) {
                    this.method = method
                    timeoutMs?.let { install(HttpTimeout) { requestTimeoutMillis = it } }
                    headers.forEach { (k, v) -> header(k, v) }
                    if (body != null) setBody(body)
                }
            }.await()

            val ctHeader = response.headers[HttpHeaders.ContentType]?.lowercase() ?: ""
            val isJson = "application/json" in ctHeader
            val isNoContent = response.status == HttpStatusCode.NoContent

            if (!response.status.isSuccess()) {
                val errBody = if (!isNoContent && isJson) runCatching { response.bodyAsText() }.getOrNull() else null
                throw HttpError(response.status.value, response.status.description, errBody)
            }

            val data: T? = if (!isNoContent && isJson) runCatching { response.body<T>() }.getOrNull() else null
            return ApiResult(response, data)
        } finally {
            // cancels any running work, mirroring AbortController cleanup
            job.cancel()
        }
    }

    suspend inline fun <reified T> get(
        path: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): T = request<T>(path, HttpMethod.Get, null, headers, timeoutMs).data as T

    suspend inline fun <reified T> post(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): T = request<T>(path, HttpMethod.Post, body, headers, timeoutMs).data as T

    suspend inline fun <reified T> put(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): T = request<T>(path, HttpMethod.Put, body, headers, timeoutMs).data as T

    suspend inline fun <reified T> patch(
        path: String,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): T = request<T>(path, HttpMethod.Patch, body, headers, timeoutMs).data as T

    suspend inline fun <reified T> delete(
        path: String,
        headers: Map<String, String> = emptyMap(),
        timeoutMs: Long? = null
    ): T = request<T>(path, HttpMethod.Delete, null, headers, timeoutMs).data as T

    fun close() = client.close()
}
