package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import nl.connectplay.scoreplay.models.SignUpRequest

class AuthApi(
    private val client: HttpClient
) {
    suspend fun registerUser(registerRequest: SignUpRequest): SignUpRequest {
        return client.post(Routes.Auth.register) {
            contentType(ContentType.Application.Json)
                setBody(registerRequest)
            }
            .body()
    }
}
