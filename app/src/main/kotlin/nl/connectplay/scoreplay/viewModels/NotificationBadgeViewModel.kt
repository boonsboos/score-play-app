package nl.connectplay.scoreplay.viewModels

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sseSession
import kotlin.time.Duration.Companion.seconds
import io.ktor.client.request.headers
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
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
    val hasUnreadNotifications =
        _hasUnreadNotifications.asStateFlow() // with the asStateFlow() you create a read-only

    // this will ignore the fields that are not defined in our data models
    private val json = Json {
        ignoreUnknownKeys = true
    }

    init {
        Log.d("BADGE_VM", "instance=${hashCode()}")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    // the app will listen as long as it is active
    fun startSse(token: String) {
        viewModelScope.launch {
            Log.d("SSE", "Starting SSE session")

            try {
                val session = httpClient.sseSession(
                    urlString = Routes.Notifications.live,
                    reconnectionTime = 15.seconds // try to reconnect after 15sec if connection gets lost
                ) {
                    // we add the header so the backend knows who is listening
                    headers {
                        append("Authorization", "Bearer $token")
                    }
                }
                // for each incoming server event call the processSseEvent function
                session.incoming.collect { processSseEvent(it) }
            } catch (e: Exception) {
                Log.e("SSE", "SSE session encountered an error ${e.message}", e)
            } finally {
                Log.w("SSE", "Closing connection!")
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun processSseEvent(sseEvent: ServerSentEvent) {
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun processEvent(event: BaseEvent) {
        when (event) {
            is FriendRequestEvent -> {
                val title = "New Friend Request"
                val message = "${event.from.username} has send you a friend request!"
                // shows the notification for the friend request event
                NotificationBuilder.showNotification(appContext, title, message)
            }

            is FriendRequestReplyEvent -> {
                val title = "Reaction to Friend Request"
                val message =
                    // because there are two options there must be a check to check if the friend request was accepted or not
                    if (event.accepts) {
                        "${event.respondingUser.username} has accepted your friend request"
                    } else {
                        "${event.respondingUser.username} has declined your friend request"
                    }
                NotificationBuilder.showNotification(appContext, title, message)
            }

            is HighscoreEvent -> {
                val title = "New highscore on game ${event.game.name}"
                val message =
                    // a guest player or a user can have a highscore
                    if (event.score.sessionPlayer.guest != null) {
                        "${event.score.sessionPlayer.guest} has a score of: ${event.score.score}!"
                    } else {
                        "${event.score.sessionPlayer.userId} has a score of: ${event.score.score}!"
                    }
                NotificationBuilder.showNotification(appContext, title, message)
            }
        }
    }

    fun setHasUnread(hasUnread: Boolean) {
        Log.d("Notifications", "$hasUnread")
        _hasUnreadNotifications.value = hasUnread
    }
}
