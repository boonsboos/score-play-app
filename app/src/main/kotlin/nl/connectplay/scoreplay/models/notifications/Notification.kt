package nl.connectplay.scoreplay.models.notifications

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val notificationId: String,
    val content: String,
    val read: Boolean,
)