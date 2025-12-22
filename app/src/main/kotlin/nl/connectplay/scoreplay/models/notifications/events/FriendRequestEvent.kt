package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("friendRequest")
class FriendRequestEvent(val targetUserId: Int) : BaseEvent()