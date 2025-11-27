package nl.connectplay.scoreplay.api.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Http {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true   // server may send extra fields
                    isLenient = true           // more flexible with JSON format
                }
            )
        }

        defaultRequest {
            url("https://pma.connectplay.local")  // BASE URL
        }
    }
}
