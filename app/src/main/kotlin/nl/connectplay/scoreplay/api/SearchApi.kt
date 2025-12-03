package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.call.body
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import nl.connectplay.scoreplay.models.dto.SearchGameDto
import nl.connectplay.scoreplay.models.dto.SearchUserDto
import nl.connectplay.scoreplay.stores.TokenDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchApi(private val client: HttpClient) : KoinComponent {

    private val tokenStore: TokenDataStore by inject()

    private fun getToken(): String? =
        runBlocking { tokenStore.token.firstOrNull() }

    suspend fun searchUsers(query: String): List<SearchUserDto> {
        return client.get("/users") {
            val token = getToken()
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
            parameter("query", query)
        }.body()
    }

    suspend fun searchGames(query: String): List<SearchGameDto> {
        return client.get("/games") {
            val token = getToken()
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
            parameter("query", query)
        }.body()
    }
}