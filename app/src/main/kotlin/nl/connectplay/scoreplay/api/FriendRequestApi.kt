package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendRequest
import nl.connectplay.scoreplay.models.FriendRequestReply

/**
 * API class for handling friend requests.
 * Provides methods to:
 * 1. Retrieve all friend requests for a user
 * 2. Accept a friend request
 * 3. Decline a friend request
 *
 * Uses Ktor HttpClient to communicate with the backend endpoints defined in Routes.
 */
class FriendRequestApi(private val client: HttpClient) {

    /**
     * Retrieves all friend requests (and possibly friends) for a given user.
     *
     * @param userId The ID of the user whose friend requests are being fetched.
     * @return A list of FriendRequest objects. Returns empty list on failure.
     */
    suspend fun getAllFriendrequests(userId: Int): List<FriendRequest> {
        return try {
            // Make a GET request to the backend route for all friend requests
            client.get(Routes.FriendRequest.getFriendRequests(userId)).body()
        } catch (e: Exception) {
            // If request fails, return an empty list
            emptyList()
        }
    }

    /**
     * Accepts a friend request for the current user.
     *
     * @param userId The ID of the user who is accepting the request.
     * @param friendId The ID of the friend request to accept.
     * @return A Friend object representing the newly added friend, or null if the request fails.
     */
    suspend fun accept(userId: Int, friendId: Int): Friend? {
        try {
            // Create request body indicating acceptance
            val body = FriendRequestReply(accept = true)

            // Make PATCH request to update the friend request status
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                setBody(body)
            }
        } catch (e: Exception) {
            // Log the error or show a toast
            println("Failed to accept friend request: ${e.message}")
        }
        return null
    }

    /**
     * Declines a friend request for the current user.
     *
     * @param userId The ID of the user who is declining the request.
     * @param friendId The ID of the friend request to decline.
     * @return True if the operation succeeded, false if it failed.
     */
    suspend fun decline(userId: Int, friendId: Int): Boolean {
        return try {
            // Create request body indicating decline
            val body = FriendRequestReply(accept = false)

            // Make PATCH request to update friend request status
            client.patch(Routes.FriendRequest.handleFriendRequest(userId, friendId)) {
                setBody(body)
            }

            // Return true if successful
            true
        } catch (e: Exception) {
            // Return false on failure
            false
        }
    }
}
