package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable


@Serializable
data class Friend(
    val id: String,
    val username: String,
    val avatarLetter: String
)

@Serializable
data class FriendRequest(
    val id: String,
    val username: String,
    val avatarLetter: String,
    val status: FriendRequestStatus
)

@Serializable
enum class FriendRequestStatus {
    PENDING,
    INCOMING,
    SENT
}