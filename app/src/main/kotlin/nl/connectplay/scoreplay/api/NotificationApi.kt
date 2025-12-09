package nl.connectplay.scoreplay.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import nl.connectplay.scoreplay.models.Notification
import nl.connectplay.scoreplay.stores.TokenDataStore

class NotificationApi(
    private val client: HttpClient,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun getAllNotifications(): List<Notification> {
        val res = client.get(Routes.Notifications.root) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(tokenDataStore.token.firstOrNull() ?: "")
        }
        return res.body()
    }
}
