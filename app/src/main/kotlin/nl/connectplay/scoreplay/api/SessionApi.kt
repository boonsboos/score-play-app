package nl.connectplay.scoreplay.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first
import nl.connectplay.scoreplay.models.dto.CreateSessionDto
import nl.connectplay.scoreplay.models.dto.CreateScoreDto
import nl.connectplay.scoreplay.stores.TokenDataStore

/**
 * Session is for all session API-calls
 *
 *
 * @property client The HttpClient used to send requests to the server
 */
class SessionApi(private val client: HttpClient, private val tokenDataStore: TokenDataStore) {
    suspend fun createSession(payload: CreateSessionDto): String {
        val resp =  client.post(Routes.Sessions.root) {
            contentType(ContentType.Application.Json)   // let the server know we will send JSON
            bearerAuth(tokenDataStore.token.first() ?: "")
            setBody(payload)
        }

        val raw = resp.bodyAsText()
        Log.d("SessionApi", "createSession -> ${resp.status}: $raw")

        if (!resp.status.isSuccess()) {
            throw RuntimeException("createSession failed: ${resp.status} $raw")
        }

        val sessionId = resp.bodyAsText().trim().removeSurrounding("\"")
        return sessionId
    }

    suspend fun addScores(sessionId: String, payload: List<CreateScoreDto>) {
        val resp = client.post(Routes.Sessions.Scores.all(sessionId)) {
            contentType(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.first() ?: "")
            setBody(payload)
        }

        val raw = resp.bodyAsText()
        Log.d("SessionApi", "addScores -> ${resp.status}: $raw")

        if (!resp.status.isSuccess()) {
            throw RuntimeException("addScores failed: ${resp.status} $raw")
        }
    }
}
