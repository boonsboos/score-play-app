package nl.connectplay.scoreplay.api;

import io.ktor.client.HttpClient;
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import nl.connectplay.scoreplay.models.Game

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
}
