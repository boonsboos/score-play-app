package nl.connectplay.scoreplay.models.friends

import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.user.BareUser

@Serializable
data class UserFriend(val user: BareUser, val status: FriendshipStatus)
