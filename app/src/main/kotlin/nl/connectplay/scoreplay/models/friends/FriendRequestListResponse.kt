package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestListResponse(val pending: List<UserFriend> = emptyList(), val outstanding: List<UserFriend> = emptyList())