package nl.connectplay.scoreplay.api.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Http {
    val client = HttpClient(OkHttp) {
        /**
         * ContentNegotiation + json ensures:
         * - Requests with a body (via setBody()) are automatically sent as JSON
         * - Responses with Content-Type: application/json are automatically deserialized
         *   into your @Serializable types
         *
         * → You do NOT need to manually set Content-Type: application/json on each request.
         */
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true    // server may send extra fields
                    prettyPrint = true          // nicely formatted JSON (mainly for debug/logging)
                    isLenient = true            // more flexible JSON parsing
                }
            )
        }
        /**
         * defaultRequest sets the base URL for all requests.
         * In your API calls you only use relative paths (e.g. "/example"),
         * Ktor will automatically prepend this base URL → https://pma.connectplay.local/example
         */
        defaultRequest {
            url("https://api.connect-en-play.nl")  // BASE URL
        }
    }
}
