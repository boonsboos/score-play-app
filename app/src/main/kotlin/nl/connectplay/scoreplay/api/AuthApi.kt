package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import nl.connectplay.scoreplay.models.auth.request.LoginRequest
import nl.connectplay.scoreplay.models.auth.response.LoginResponse

class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post(Routes.Auth.login) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}