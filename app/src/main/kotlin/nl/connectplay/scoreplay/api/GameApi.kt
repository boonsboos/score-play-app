package nl.connectplay.scoreplay.api;

import androidx.compose.ui.graphics.vector.ImageVector
import io.ktor.client.HttpClient;
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import nl.connectplay.scoreplay.models.game.Game

class GameApi(val client: HttpClient) {
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
            client.get(Routes.Games.single(gameId)).body()
        } catch (e: NoTransformationFoundException) {
            null
        }

    suspend fun pictures(gameId: Int): List<String> =
        try {
            client.get(Routes.Games.pictures(gameId)).body()
        } catch (e: NoTransformationFoundException) {
            listOf()
        }
}
