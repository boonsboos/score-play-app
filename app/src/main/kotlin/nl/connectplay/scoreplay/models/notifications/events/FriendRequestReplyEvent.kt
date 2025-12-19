package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("friendRequestReply")
class FriendRequestReplyEvent(val friendId: Int, val accepted: Boolean) : BaseEvent()