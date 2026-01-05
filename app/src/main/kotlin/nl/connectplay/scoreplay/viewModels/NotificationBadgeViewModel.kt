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
import nl.connectplay.scoreplay.api.Routes


class NotificationBadgeViewModel(private val httpClient: HttpClient) : ViewModel() {
    private val _hasUnreadNotifications = MutableStateFlow(false)
    val hasUnreadNotifications = _hasUnreadNotifications.asStateFlow()

    fun startSse(token: String) {
        viewModelScope.launch {
            val session = httpClient.sseSession(
                urlString = Routes.Notifications.live,
                reconnectionTime = 15.seconds
            ) {
                headers {
                    append("Authorization", "Bearer $token")
                }
            }

            session.incoming.collect {
                showBadge()
            }
//            try {
//
//            } catch (e: Exception) {
//                Log.w("SSE", "SSE is disabled for testing ${e.message})")
//            }
        }
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
