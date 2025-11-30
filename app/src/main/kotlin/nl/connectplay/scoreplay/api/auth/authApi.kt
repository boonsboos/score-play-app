package nl.connectplay.scoreplay.api.auth

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import nl.connectplay.scoreplay.api.Routes
import nl.connectplay.scoreplay.api.auth.request.LoginRequest
import nl.connectplay.scoreplay.api.auth.response.LoginResponse

class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post(Routes.Auth.login) {
            setBody(request)
        }.body()
    }
}