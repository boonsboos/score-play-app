package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import nl.connectplay.scoreplay.models.Friend

/**
 * API class for retrieving friends of a user.
 * Uses Ktor HttpClient to make GET requests to the backend.
 */
class FriendsApi(private val client: HttpClient) {

    /**
     * Retrieves all friends for a given user by their ID.
     *
     * @param userId The ID of the user whose friends are being fetched.
     * @return A list of Friend objects. Returns an empty list if the request fails or no data is found.
     */
    suspend fun getFriendsById(userId: Int): List<Friend> {
        return try {
            // Make a GET request to the backend route for the user's friends
            client.get(Routes.Friend.friends(userId)).body<List<Friend>>()


        } catch (e: NoTransformationFoundException) {
            // If the response body cannot be transformed into List<Friend>, return an empty list
            listOf()
        }
    }
}
