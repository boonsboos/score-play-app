package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestResponse(val friendId: Int, val status: FriendshipStatus = FriendshipStatus.PENDING)
