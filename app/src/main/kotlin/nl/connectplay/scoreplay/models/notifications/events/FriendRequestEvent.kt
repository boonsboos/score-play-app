package nl.connectplay.scoreplay.models.notifications.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.user.BareUser

@Serializable
@SerialName("friendRequest")
class FriendRequestEvent(val from: BareUser) : BaseEvent()