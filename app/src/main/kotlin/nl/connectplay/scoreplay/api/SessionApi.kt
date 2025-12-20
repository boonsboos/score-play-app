package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import nl.connectplay.scoreplay.models.session.CreateSessionRequest
import nl.connectplay.scoreplay.models.session.CreateSessionResponse

/**
 * Session is for all session API-calls
 *
 *
 * @property client The HttpClient used to send requests to the server
 */
class SessionApi(private val client: HttpClient) {
    suspend fun createSession(payload: CreateSessionRequest): CreateSessionResponse {
        return client.post(Routes.Sessions.root) {
            contentType(ContentType.Application.Json)   // let the server know we will send JSON
            setBody(payload)
        }.body()
    }
}
