package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sseSession
import kotlin.time.Duration.Companion.seconds
import io.ktor.client.request.headers
import io.ktor.sse.ServerSentEvent
import kotlinx.serialization.json.Json
import nl.connectplay.scoreplay.api.Routes
import nl.connectplay.scoreplay.models.notifications.events.BaseEvent


class NotificationBadgeViewModel(private val httpClient: HttpClient) : ViewModel() {
    private val _hasUnreadNotifications = MutableStateFlow(false)
    val hasUnreadNotifications = _hasUnreadNotifications.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun startSse(token: String) {
        viewModelScope.launch {
            Log.d("SSE", "Starting SSE session")
            try {
                val session = httpClient.sseSession(
                    urlString = Routes.Notifications.live,
                    reconnectionTime = 15.seconds
                ) {
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }

                session.incoming.collect { processSseEvent(it) }
            } catch (e: Exception) {
                Log.w("SSE", "SSE session encountered an error ${e.message}", e)
            }
        }
    }

    private suspend fun processSseEvent(sseEvent: ServerSentEvent) {
        Log.d("SSE", "Received event from server ${sseEvent.data}")

        try {
            // decode the json string, same as the Notification.content
            val eventModel = json.decodeFromString<BaseEvent>(sseEvent.data ?: "")

            // further processing
            processEvent(eventModel)

            showBadge() // notify the user they have a notification, in the app itself
        } catch (e: Exception) {
            Log.e("SSE", "Failed to decode event: ${e.message}", e)
        }
    }

    private suspend fun processEvent(event: BaseEvent) {
        TODO("Show push notification")
    }

    fun showBadge() {
        _hasUnreadNotifications.value =
            true // update the state to check if there is at least one unread notification
    }

    fun clearBadge() {
        _hasUnreadNotifications.value =
            false // resets the badge state so if the user has seen the notifications
    }

}
