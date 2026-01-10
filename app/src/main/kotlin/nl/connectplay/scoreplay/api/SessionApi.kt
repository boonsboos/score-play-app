package nl.connectplay.scoreplay.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.io.asSource
import kotlinx.io.buffered
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.dto.CreateSessionDto
import nl.connectplay.scoreplay.models.dto.CreateScoreDto
import nl.connectplay.scoreplay.models.dto.UpdateSessionDto
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.stores.TokenDataStore
import java.io.InputStream

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

    suspend fun update(sessionId: String, updateSessionDto: UpdateSessionDto): Boolean {
        try {
            val res = client.patch(Routes.Sessions.byId(sessionId)) {
                contentType(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
                setBody(updateSessionDto)
            }

            return res.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e(this::class.simpleName, "Failed to update session $sessionId", e)
            return false
        }
    }

    suspend fun addEndPicture(sessionId: String, dataInputStream: InputStream): Boolean {
        try {
            val imageData = MultiPartFormDataContent(
                formData {
                    appendInput(key = sessionId, headers = Headers.build {
                        append(
                            HttpHeaders.ContentDisposition,
                            "form-data; name=\"$sessionId\"; filename=\"$sessionId\""
                        )
                        append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                    }) {
                        dataInputStream.use { it.asInput() }
                    }
                }
            )

            val res = client.patch(Routes.Sessions.picture(sessionId)) {
                contentType(ContentType.MultiPart.FormData)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
                setBody(imageData)
            }

            return res.status == HttpStatusCode.OK
        } catch (e: Exception) {
            Log.e(this::class.simpleName, "Failed to upload session end picture for session $sessionId", e)
            return false
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