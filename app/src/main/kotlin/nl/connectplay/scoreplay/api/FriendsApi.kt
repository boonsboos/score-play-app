package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.models.friends.*
import nl.connectplay.scoreplay.stores.TokenDataStore

/**
 * API for managing friends and friend requests:
 * - Fetch friends
 * - Fetch friend requests
 * - Handle friend requests
 */
class FriendsApi(
    private val client: HttpClient,
    private val tokenDataStore: TokenDataStore
    ) {


    /**
     * Fetch friends
     */
    suspend fun getFriends(userId: Int): List<UserFriend> {
        return try {
            val res = client.get(Routes.Friends.getFriends(userId)) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }
            res.body()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    /**
     * Fetch friendrequests
     */
    suspend fun getAllFriendRequests(): FriendRequestListResponse =try {
             client.get(Routes.FriendRequest.getAllFriendRequests) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }.body()
        } catch (e: NoTransformationFoundException) {
            e.printStackTrace()
            FriendRequestListResponse(emptyList(), emptyList())
        }

    /**
     * Handle friendrequests
     */
    suspend fun accept(friendId: Int): Boolean {
        val userId = tokenDataStore.userId.firstOrNull() ?: return false

        return try {
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
                setBody(mapOf("accept" to true))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun decline(friendId: Int): Boolean {
        val userId = tokenDataStore.userId.firstOrNull() ?: return false

        return try {
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
                setBody(mapOf("accept" to false))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}