package nl.connectplay.scoreplay.api

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

object Http {
    private val client = ApiClient()

    private fun headers(extra: Map<String, String> = emptyMap()): Map<String, String> =
        mapOf(HttpHeaders.ContentType to ContentType.Application.Json.toString()) + extra

    suspend inline fun <reified T> get(p: String, timeoutMs: Long? = null): T =
        client.get<T>(p, headers(), timeoutMs)

    suspend inline fun <reified T> post(p: String, b: Any? = null, timeoutMs: Long? = null): T =
        client.post<T>(p, b, headers(), timeoutMs)

    suspend inline fun <reified T> put(p: String, b: Any? = null, timeoutMs: Long? = null): T =
        client.put<T>(p, b, headers(), timeoutMs)

    suspend inline fun <reified T> patch(p: String, b: Any? = null, timeoutMs: Long? = null): T =
        client.patch<T>(p, b, headers(), timeoutMs)

    suspend inline fun <reified T> delete(p: String, timeoutMs: Long? = null): T =
        client.delete<T>(p, headers(), timeoutMs)
}
