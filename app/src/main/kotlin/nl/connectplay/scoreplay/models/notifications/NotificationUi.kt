package nl.connectplay.scoreplay.models.notifications

import nl.connectplay.scoreplay.models.notifications.events.BaseEvent

/**
 * specific model for displaying notifications.
 *
 * @param notificationId the identifier of the notification
 * @param event is used to display the notification in the UI
 * @param read mark the message if it hase been read or not
 */

data class NotificationUi(
    val notificationId: String,
    val event: BaseEvent,
    val read: Boolean
)