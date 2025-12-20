package nl.connectplay.scoreplay.api;

import android.util.Log
import io.ktor.client.HttpClient;
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.game.GameDetailDto
import nl.connectplay.scoreplay.stores.TokenDataStore

class GameApi(val client: HttpClient, val tokenDataStore: TokenDataStore) {
    private val LOG_TAG = this::class.simpleName

    suspend fun all(offset: Int = 0, limit: Int = 25): List<Game> {
        return try {
            client.get(Routes.Games.getAll()) {
                parameter("offset", offset)
                parameter("limit", limit)
            }.body()
        } catch (_: NoTransformationFoundException) {
            listOf()
        }
    }

    suspend fun single(gameId: Int): GameDetailDto? =
        try {
            val res = client.get(Routes.Games.single(gameId)) {
                bearerAuth(tokenDataStore.token.first() ?: "")
            }

            if (res.status != HttpStatusCode.OK) {
                Log.e(LOG_TAG, "Failed to get game $gameId, ${res.status}")
            }

            res.body()
        } catch (_: NoTransformationFoundException) {
            Log.i(LOG_TAG, "No mapping found for game")
            null
        }

    suspend fun unfollow(gameId: Int): Boolean {
        try {
            val res = client.delete(Routes.Games.unfollow(gameId)) {
                bearerAuth(tokenDataStore.token.first() ?: "")
            }

            return res.status == HttpStatusCode.OK
        } catch(e: Exception) {
            Log.e(LOG_TAG, "Failed to unfollow game $gameId: ${e.message}", e)
            return false
        }
    }

    suspend fun follow(gameId: Int): Boolean {
        try {
            val res = client.post(Routes.Games.follow(gameId)) {
                bearerAuth(tokenDataStore.token.first() ?: "")
            }

            return res.status == HttpStatusCode.Created
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to follow game $gameId: ${e.message}", e)
            return false
        }
    }
}
