package nl.connectplay.scoreplay.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.first
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendDto
import nl.connectplay.scoreplay.stores.TokenDataStore

class FriendsApi(
    private val client: HttpClient,
    private val tokenDataStore: TokenDataStore
) {

    /**
     * Fetches the friends of a user.
     * Since the backend returns a list of FriendDto objects, we map them to
     * frontend Friend models with an avatar letter for display purposes.
     *
     * @param userId The ID of the user whose friends are being fetched
     * @return List of Friend objects for UI display
     */
    suspend fun getFriendsById(userId: Int): List<Friend> {
        // Get the JWT token from the datastore
        val token = tokenDataStore.token.first() ?: return emptyList()

        return try {
            // Make the GET request with the Authorization header
            val friendDto: List<FriendDto> = client.get("/users/$userId/friends") {
                header("Authorization", "Bearer $token")
            }.body() // explicitly specify type for deserialization

            // Map the backend DTOs to frontend Friend models
            friendDto.map { dto ->
                Friend(
                    username = dto.username,
                    avatarLetter = dto.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
                )
            }.sortedBy { it.username.lowercase() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return empty list on any error
        }
    }
}
