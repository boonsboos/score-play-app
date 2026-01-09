package nl.connectplay.scoreplay.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.dto.CreateSessionDto
import nl.connectplay.scoreplay.models.dto.CreateScoreDto
import nl.connectplay.scoreplay.models.dto.ScoreDto
import nl.connectplay.scoreplay.models.session.Session
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

    suspend fun allScores(sessionId: String): List<ScoreDto> {
        return try {
            client.get(Routes.Sessions.Scores.all(sessionId)) {
                contentType(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.first() ?: "")
            }.body()

        } catch (_: NoTransformationFoundException) {
            Log.d("ScoresApiCall", "The scores are NOT given!")
            listOf()
        }
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