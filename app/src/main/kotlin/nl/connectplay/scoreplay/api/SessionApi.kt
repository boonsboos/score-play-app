package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.session.CreateSessionRequest
import nl.connectplay.scoreplay.models.session.CreateSessionResponse
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.stores.TokenDataStore

/**
 * Session is for all session API-calls
 *
 *
 * @property client The HttpClient used to send requests to the server
 */
class SessionApi(private val client: HttpClient, private val tokenDataStore: TokenDataStore) {
    suspend fun createSession(payload: CreateSessionRequest): CreateSessionResponse {
        return client.post(Routes.Sessions.root) {
            contentType(ContentType.Application.Json)   // let the server know we will send JSON
            setBody(payload)
        }.body()
    }

    suspend fun single(userId: Int, sessionId: String): Session {
        try {
            val res = client.get(Routes.Sessions.byUserAndSessionId(userId, sessionId)) {
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }

            return when (res.status.value) {
                200 -> res.body()

                401 -> {
                    tokenDataStore.clearToken()
                    throw InvalidTokenException("Invalid or expired token")
                }

                else -> {
                    throw Exception("Failed to get session (status ${res.status.value})")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to get session", e)
        }
    }

    suspend fun delete(sessionId: String) {
        try {
            val res = client.delete(Routes.Sessions.byId(sessionId)) {
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }

            when (res.status.value) {
                200, 204 -> {
                }
                401 -> {
                    tokenDataStore.clearToken()
                    throw InvalidTokenException("Invalid or expired token")
                }
                else -> {
                    throw Exception("Failed to delete session (status ${res.status.value})")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to delete session", e)
        }
    }

}