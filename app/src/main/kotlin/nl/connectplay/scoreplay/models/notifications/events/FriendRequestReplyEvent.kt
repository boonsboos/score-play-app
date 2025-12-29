package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.user.BareUser

@Serializable
@SerialName("friendRequestReply")
class FriendRequestReplyEvent(val respondingUser: BareUser, val accepts: Boolean) : BaseEvent()