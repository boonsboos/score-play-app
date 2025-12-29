package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
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
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.io.asSource
import kotlinx.io.buffered
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.models.user.UserSession
import nl.connectplay.scoreplay.stores.TokenDataStore

class ProfileApi(
    val client: HttpClient, private val tokenDataStore: TokenDataStore
) {
    suspend fun getProfile(userId: Int? = null): UserProfile = try {
        val res = client.get(if (userId != null) Routes.Users.byId(userId) else Routes.Users.me) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }
        if (res.status.value == 401) {
            tokenDataStore.clearToken()
            throw InvalidTokenException("Invalid or expired token")
        } else {
            res.body()
        }
    } catch (e: NoTransformationFoundException) {
        e.printStackTrace()
        throw Exception("Failed to fetch profile data", e)
    }

    suspend fun getLastSessions(userId: Int): List<UserSession> = try {
        val res = client.get(Routes.Users.sessions(userId) + "?limit=5") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }
        when (res.status.value) {
            204 -> {
                emptyList()
            }

            502 -> {
                throw Exception("Bad Gateway")
            }

            else -> {
                res.body()
            }
        }
    } catch (e: NoTransformationFoundException) {
        e.printStackTrace()
        throw Exception("Failed to fetch last sessions", e)
    }

    suspend fun getFollowedGames(userId: Int): List<Game> = try {
        val res = client.get(Routes.Users.followedGames(userId)) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }

        when (res.status.value) {
            204 -> {
                emptyList()
            }

            502 -> {
                throw Exception("Bad Gateway")
            }

            else -> {
                res.body()
            }
        }
    } catch (e: NoTransformationFoundException) {
        e.printStackTrace()
        throw Exception("Failed to fetch followed games", e)
    }

    suspend fun deleteAccount() {
        try {
            val res = client.delete(Routes.Users.me) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }

            when (res.status.value) {
                /**
                 *200 for OK
                 * 204 for No Content
                 */
                200, 204 -> {
                    tokenDataStore.clearToken()
                }
                /**
                 * No authorization
                 */
                401 -> {
                    tokenDataStore.clearToken()
                    throw InvalidTokenException("Invalid or expired token")
                }

                else -> {
                    throw Exception("Failed to delete account (status ${res.status.value})")
                }
            }
        } catch (e: NoTransformationFoundException) {
            e.printStackTrace()
            throw Exception("Failed to delete account", e)
        }
    }

    suspend fun updateProfile(username: String, email: String): UserProfile = try {
        val res = client.patch(Routes.Users.me) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            setBody(mapOf("username" to username, "email" to email))
        }
        if (res.status.value == 401) {
            tokenDataStore.clearToken()
            throw InvalidTokenException("Invalid or expired token")
        } else {
            res.body()
        }
    } catch (e: NoTransformationFoundException) {
        e.printStackTrace()
        throw Exception("Failed to update profile data", e)
    }

    suspend fun uploadProfilePicture(bytes: ByteArray): String = try {
        val res = client.post(Routes.Users.uploadPicture) {
            contentType(ContentType.MultiPart.FormData)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            // Set the body as multipart form data
            setBody(
                MultiPartFormDataContent(
                    formData {
                        appendInput(
                            key = "file", // MUST match backend field name
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"file\"; filename=\"picture.jpg\""
                                )
                                append(HttpHeaders.ContentType, ContentType.Image.JPEG)
                            }) {
                            bytes.inputStream().asSource().buffered()
                        }
                    })
            )
        }
        if (res.status.value == 401) {
            tokenDataStore.clearToken()
            throw InvalidTokenException("Invalid or expired token")
        } else {
            res.body()
        }
    } catch (e: NoTransformationFoundException) {
        e.printStackTrace()
        throw Exception("Failed to upload profile picture", e)
    }
}