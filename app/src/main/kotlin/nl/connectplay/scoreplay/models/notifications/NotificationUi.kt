package nl.connectplay.scoreplay.models.notifications

import nl.connectplay.scoreplay.models.notifications.events.BaseEvent

data class NotificationUi(
    val notificationId: String,
    val event: BaseEvent,
    val read: Boolean
)