package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestReply(
    val accept: Boolean
)
