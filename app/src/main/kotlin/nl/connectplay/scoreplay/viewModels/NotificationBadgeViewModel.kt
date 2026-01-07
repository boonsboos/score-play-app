package nl.connectplay.scoreplay.viewModels

import android.content.Context
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
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestReplyEvent
import nl.connectplay.scoreplay.models.notifications.events.HighscoreEvent
import nl.connectplay.scoreplay.ui.notifications.NotificationBuilder

class NotificationBadgeViewModel(
    private val httpClient: HttpClient,
    private val appContext: Context
) : ViewModel() {
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
                Log.e("SSE", "SSE session encountered an error ${e.message}", e)
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

        } catch (e: Exception) {
            Log.e("SSE", "Failed to decode event: ${e.message}", e)
        }
    }

    private suspend fun processEvent(event: BaseEvent) {
        Log.d("SSE_NOTIFY", "processEvent called with ${event::class.simpleName}")
        when (event) {
            is FriendRequestEvent -> {
                val title = "New Friendsrequest"
                val message = "${event.from.username} has send you a friendrequest!"
                // shows the notification for the friend request event
                NotificationBuilder.showNotification(appContext, title, message)
            }

            is FriendRequestReplyEvent -> {
                val title = "Reaction of friendsrequest"
                val message =
                    // because there are two options there must be a check to check if the friend request was accepted or not
                    if (event.accepts) {
                        "${event.respondingUser.username} has accepted your friendrequest"
                    } else {
                        "${event.respondingUser.username} has declined your friendrequest"
                    }
                NotificationBuilder.showNotification(appContext, title, message)
            }

            is HighscoreEvent -> {
                val title = "Nieuwe highscore on game ${event.game.name}"
                val message = if (event.score.sessionPlayer.guest != null) {
                    "${event.score.sessionPlayer.guest} has a score of: ${event.score.score}!"
                } else {
                    "${event.score.sessionPlayer.userId} has a score of: ${event.score.score}!"
                }
                NotificationBuilder.showNotification(appContext, title, message)
            }
        }
    }

    fun setHasUnread(hasUnread: Boolean) {
        _hasUnreadNotifications.value = hasUnread
    }
}
