package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.call.body
import nl.connectplay.scoreplay.models.dto.search.SearchUserDto
import nl.connectplay.scoreplay.models.dto.search.SearchGameDto

class SearchApi(private val client: HttpClient) {

    suspend fun searchUsers(query: String): List<SearchUserDto> {
        return client.get("/users") {
            parameter("query", query)
        }.body()
    }

    suspend fun searchGames(query: String): List<SearchGameDto> {
        return client.get("/games") {
            parameter("query", query)
        }.body()
    }
}