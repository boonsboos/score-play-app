package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestListResponse(val pending: List<UserFriend>, val outstanding: List<UserFriend>)