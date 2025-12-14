package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.user.UserProfile

@Serializable
data class UserFriend(val user: UserProfile, val status: FriendshipStatus)
