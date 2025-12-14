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
     * Adds the necessary authentication and content headers to an HTTP request.
     *
     * This function retrieves the current user token from the TokenDataStore.
     * If a token is available, it sets the "Authorization" header with a Bearer token
     * and also sets the "Accept" header to "application/json".
     *
     * @param builder The HttpRequestBuilder to which the headers will be added.
     * @return true if the token was successfully retrieved and headers were added, false otherwise.
     * */
    private suspend fun authHeaders(builder: HttpRequestBuilder): Boolean {
        val token = tokenDataStore.token.firstOrNull() ?: return false
        builder.header("Authorization", "Bearer $token")
        builder.header("Accept", "application/json")
        return true
    }

    /**
     * Fetch friends
     */
    suspend fun getFriends(userId: Int): List<UserFriend> {
        return client.get(Routes.Friends.getFriends(userId)) {
            if (!authHeaders(this)) return emptyList()
        }.body()
    }

    /**
     * Fetch friendrequests
     */
    suspend fun getAllFriendRequests(): FriendRequestListResponse {
        return try {
            client.get(Routes.FriendRequest.getAllFriendRequests()) {
                if (!authHeaders(this)) return FriendRequestListResponse(emptyList(), emptyList())
            }.body()
        } catch (e: NoTransformationFoundException) {
            FriendRequestListResponse(emptyList(), emptyList())
        }
    }

    /**
     * Handle friendrequests
     */
    suspend fun accept(friendId: Int): Boolean {
        val userId = tokenDataStore.userId.firstOrNull() ?: return false

        return try {
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                if (!authHeaders(this)) return false
                contentType(ContentType.Application.Json)
                setBody(FriendRequestReply(accept = true))
            }
            true
        } catch (e: NoTransformationFoundException) {
            false
        }
    }

    suspend fun decline(friendId: Int): Boolean {
        val userId = tokenDataStore.userId.firstOrNull() ?: return false

        return try {
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                if (!authHeaders(this)) return false
                contentType(ContentType.Application.Json)
                setBody(FriendRequestReply(accept = false))
            }
            true
        } catch (e: NoTransformationFoundException) {
            false
        }
    }
}