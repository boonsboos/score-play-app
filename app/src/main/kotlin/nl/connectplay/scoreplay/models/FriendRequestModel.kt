package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: Int,
    val username: String,
    val avatarLetter: String,
    val status: FriendRequestStatus
)
