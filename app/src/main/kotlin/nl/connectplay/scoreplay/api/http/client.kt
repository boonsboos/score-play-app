package nl.connectplay.scoreplay.api.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.sse.SSE
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

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
         * Install the SSE Plugin so the client can open SSE-events
         */
        install(SSE)

        /**
         * defaultRequest sets the base URL for all requests.
         * In your API calls you only use relative paths (e.g. "/example"),
         * Ktor will automatically prepend this base URL → https://api.connect-en-play.nl/example
         */
        defaultRequest {
            url("https://api.connect-en-play.nl")  // BASE URL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 20_000 // 20 seconds
            socketTimeoutMillis = 20_000 // 20 seconds
        }
    }
}
