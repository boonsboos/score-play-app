package nl.connectplay.scoreplay.api;

import android.util.Log
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.Path
import io.ktor.client.HttpClient;
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.stores.TokenDataStore

class GameApi(val client: HttpClient, val tokenDataStore: TokenDataStore) {
    private val LOG_TAG = this::class.simpleName

    suspend fun all(offset: Int = 0, limit: Int = 25): List<Game> {
        return try {
            client.get(Routes.Games.getAll()) {
                parameter("offset", offset)
                parameter("limit", limit)
            }.body()
        } catch (e: NoTransformationFoundException) {
            listOf()
        }
    }

    suspend fun single(gameId: Int): Game? =
        try {
            val res = client.get(Routes.Games.single(gameId)) {
                bearerAuth(tokenDataStore.token.first() ?: "")
            }

            if (res.status != HttpStatusCode.OK) {
                Log.e(LOG_TAG, "Failed to get game $gameId, ${res.status}")
            }

            res.body()
        } catch (e: NoTransformationFoundException) {
            Log.i(LOG_TAG, "No mapping found for game")
            null
        }

    suspend fun pictures(gameId: Int): List<String> =
        try {
            client.get(Routes.Games.pictures(gameId)).body()
        } catch (e: NoTransformationFoundException) {
            listOf()
        }
}
