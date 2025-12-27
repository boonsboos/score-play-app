package nl.connectplay.scoreplay.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.models.leaderboard.LeaderboardEntry
import nl.connectplay.scoreplay.stores.TokenDataStore

class LeaderboardApi(private val httpClient: HttpClient, private val tokenDataStore: TokenDataStore) {
    private val LOG_TAG = this::class.simpleName

    suspend fun getScoresFor(gameId: Int): List<LeaderboardEntry> {
        val response = httpClient.get(Routes.Games.Leaderboard.scores(gameId)) {
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }

        if (response.status != HttpStatusCode.OK) {
            Log.d(LOG_TAG, "Failed to fetch leaderboard scores: received code ${response.status} from API")
            return listOf()
        }

        return response.body()
    }
}