package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendDto(
    val username: String,
    val profilePicture: String? = null,
    val status: String
)
