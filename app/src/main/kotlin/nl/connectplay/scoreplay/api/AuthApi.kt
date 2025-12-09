package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import nl.connectplay.scoreplay.models.auth.register.RegisterRequest
import nl.connectplay.scoreplay.models.auth.login.LoginRequest
import nl.connectplay.scoreplay.models.auth.login.LoginResponse

/**
 * AuthApi is for all authentication API-calls
 *
 * Sends login requests to the API and gets a response
 *
 * @property client The HttpClient used to send requests to the server
 */
class AuthApi(private val client: HttpClient) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post(Routes.Auth.login) {
            contentType(ContentType.Application.Json)   // let the server know we will send JSON
            setBody(request) // put the login data into the request
        }.body() // get the servers JSON and changed it in a LoginResponse object
    }

    suspend fun registerUser(registerRequest: RegisterRequest): RegisterRequest {
        return client.post(Routes.Auth.register) {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }
            .body()
    }
}
