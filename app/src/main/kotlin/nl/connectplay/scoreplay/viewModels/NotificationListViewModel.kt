package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.NotificationApi
import nl.connectplay.scoreplay.models.notifications.NotificationFilter
import nl.connectplay.scoreplay.models.notifications.events.BaseEvent
import kotlinx.serialization.json.Json
import nl.connectplay.scoreplay.models.notifications.NotificationUi
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestReplyEvent
import nl.connectplay.scoreplay.models.notifications.events.HighscoreEvent
import java.util.UUID

class NotificationListViewModel(private val notificationApi: NotificationApi) : ViewModel() {
    private val _state = MutableStateFlow<List<NotificationUi>>(emptyList())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _allNotifications = MutableStateFlow<List<NotificationUi>>(emptyList())

    private val _filter = MutableStateFlow(NotificationFilter.ALL)
    val filter = _filter.asStateFlow()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.update { true }
            _error.update { null }

            try {
                val response = notificationApi.getAllNotifications()

                _allNotifications.update {
                    response.map { notification ->
                        NotificationUi(
                            notificationId = notification.notificationId,
                            event = json.decodeFromString<BaseEvent>(notification.content),
                            read = notification.read
                        )
                    }
                }

                applyFilter()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.update { e.message ?: "A error occurred" }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun setFilter(newFilter: NotificationFilter) {
        // update the current filter state with the new filter
        _filter.value = newFilter
        // store the newly sorted notifications
        applyFilter()
    }

    fun applyFilter() {
        val allNotifications = _allNotifications.value

        val filtered = when (_filter.value) {
            NotificationFilter.ALL -> allNotifications
            NotificationFilter.UNREAD -> allNotifications.filter { !it.read }
            NotificationFilter.FRIEND_REQUEST -> allNotifications.filter { it.event is FriendRequestEvent }
            NotificationFilter.REPLIES -> allNotifications.filter { it.event is FriendRequestReplyEvent }
            NotificationFilter.HIGHSCORES -> allNotifications.filter { it.event is HighscoreEvent }
        }

        _state.value = filtered
    }

    fun markNotificationsAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationApi.markNotificationsAsRead(notificationId)

                _state.update { currentList ->
                    currentList.map { item ->
                        if (item.notificationId == notificationId)
                            item.copy(read = true)
                        else item
                    }
                }

                _allNotifications.update { currentList ->
                    currentList.map { item ->
                        if (item.notificationId == notificationId)
                            item.copy(read = true)
                        else item
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to update notification"
            }
        }
    }
}