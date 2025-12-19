package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.models.user.UserSession
import nl.connectplay.scoreplay.stores.TokenDataStore

class ProfileApi(
    val client: HttpClient,
    private val tokenDataStore: TokenDataStore
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
            val res = client.delete(Routes.Users.deleteMe()) {
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
}