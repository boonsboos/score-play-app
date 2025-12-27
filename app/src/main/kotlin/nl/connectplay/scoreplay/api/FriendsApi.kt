package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.friends.*
import nl.connectplay.scoreplay.stores.TokenDataStore

class FriendsApi(
    private val client: HttpClient,
    private val tokenDataStore: TokenDataStore
) {

    suspend fun getFriends(userId: Int): List<UserFriend> {
        return try {
            val res = client.get(Routes.Friends.getFriends(userId)) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }
            res.body<List<UserFriend>>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAllFriendRequests(): FriendRequestListResponse = try {
             client.get(Routes.FriendRequest.getAllFriendRequests) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            }.body()
        } catch (e: NoTransformationFoundException) {
            e.printStackTrace()
            FriendRequestListResponse(emptyList(), emptyList())
        }

    suspend fun addFriend(targetUserId: Int) {
        val userId = tokenDataStore.userId.firstOrNull()
            ?: throw IllegalStateException("User not logged in")

        val res = client.post(Routes.FriendRequest.addFriend(userId)) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            setBody(mapOf("friendId" to targetUserId))
        }

        when (res.status.value) {
            200, 201 -> Unit
            401 -> {
                tokenDataStore.clearToken()
                throw InvalidTokenException("Invalid or expired token")
            }
            409 -> throw Exception("Friend request already exists")
            else -> throw Exception("Failed to send friend request (${res.status})")
        }
    }

    suspend fun deleteFriend(friendId: Int) {
        val userId = tokenDataStore.userId.firstOrNull()
            ?: throw IllegalStateException("User not logged in")

        val res = client.delete(
            Routes.FriendRequest.removeFriend(userId, friendId)
        ) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }

        when (res.status.value) {
            200, 204 -> Unit
            401 -> {
                tokenDataStore.clearToken()
                throw InvalidTokenException("Invalid or expired token")
            }
            403 -> throw Exception("Not allowed to remove friend")
            404 -> throw Exception("Friend not found")
            else -> throw Exception("Failed to delete friend (${res.status})")
        }
    }

    private suspend fun reply(friendId: Int, accept: Boolean): Boolean {
        val userId = tokenDataStore.userId.firstOrNull() ?: return false

        return client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
            setBody(mapOf("accept" to accept))
        }.status == HttpStatusCode.OK
    }

    suspend fun accept(friendId: Int): Boolean = reply(friendId = friendId, accept = true)
    suspend fun decline(friendId: Int): Boolean = reply(friendId = friendId, accept = false)

}