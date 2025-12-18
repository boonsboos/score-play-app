package nl.connectplay.scoreplay.models.notifications

import kotlinx.serialization.Serializable

/**
 * Data model for notifications that are coming from the backend
 *
 *@param notificationId the identifier of the notification
 * @param content the text shown to the user in the notifications-list
 * @param read mark the message if it hase been read or not
 */
@Serializable
data class Notification(
    val notificationId: String,
    val content: String,
    val read: Boolean,
    val notificationType: NotificationType,
)