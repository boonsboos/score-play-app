package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable

@Serializable
data class NewFriendRequest(val friendId: Int)