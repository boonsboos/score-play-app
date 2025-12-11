package nl.connectplay.scoreplay.models

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val notificationId: String,
    val content: String,
    val read: Boolean,
)