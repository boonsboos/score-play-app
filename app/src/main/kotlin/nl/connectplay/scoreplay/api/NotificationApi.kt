package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import nl.connectplay.scoreplay.models.Notification

class NotificationApi(
    private val client: HttpClient
) {
    suspend fun getAllNotifications(): List<Notification> {
        return client.get(Routes.Notifications.root).body()
    }
}
