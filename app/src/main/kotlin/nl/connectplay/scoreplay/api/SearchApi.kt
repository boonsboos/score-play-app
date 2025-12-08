package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.call.body
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.models.dto.SearchGameDto
import nl.connectplay.scoreplay.models.dto.SearchUserDto
import nl.connectplay.scoreplay.stores.TokenDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * The searchAPI is responsible to get the informatie of users and games
 * The Http request will send requests to the API and the API will respond
 *
 * @property client this is the Ktor instance of the HttpClient
 */
class SearchApi(private val client: HttpClient) : KoinComponent {

    // koin wil search for the right instance of TokenDataStore and gives this to the class
    private val tokenStore: TokenDataStore by inject()

    // returns the stored token or null if the token doesnt exists
    private suspend fun getToken(): String? = tokenStore.token.firstOrNull()

    suspend fun searchUsers(query: String): List<SearchUserDto> {
        val token = getToken()
        // do the GET request at the API to get all users
        val response = client.get("/users") {
            parameter("query", query) // add the search item to the request
            if (token != null) {
                header(
                    "Authorization",
                    "Bearer $token"
                ) // add the JWT token to the request for the authentication
            }
        }

        // get the HTTP status code so we can handle the response
        return when (response.status.value) {
            200 -> response.body()
            401 -> emptyList()
            else -> emptyList()
        }
    }

    suspend fun searchGames(query: String): List<SearchGameDto> {
        val token = getToken()

        val response = client.get("/games") {
            parameter("query", query)
            if (token != null) {
                header("Authorization", "Bearer $token")
            }
        }
        return when (response.status.value) {
            200 -> response.body()
            401 -> emptyList()
            else -> emptyList()
        }
    }
}