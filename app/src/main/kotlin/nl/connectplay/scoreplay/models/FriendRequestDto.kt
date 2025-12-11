package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

/**
 * DTO representing a friend request.
 * Mirrors the backend response for pending/outstanding friend requests.
 */
@Serializable
data class FriendRequestDto(
    val user: UserDto,
    val status: FriendshipStatus
)
