package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.NotificationApi
import nl.connectplay.scoreplay.models.notifications.NotificationFilter
import nl.connectplay.scoreplay.models.notifications.events.BaseEvent
import kotlinx.serialization.json.Json
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.models.notifications.NotificationUi
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestReplyEvent
import nl.connectplay.scoreplay.models.notifications.events.HighscoreEvent
import nl.connectplay.scoreplay.models.user.UserProfile

class NotificationListViewModel(private val notificationApi: NotificationApi, private val profileApi: ProfileApi) : ViewModel() {
    // the currently visible notifications for the UI
    private val _state = MutableStateFlow<List<NotificationUi>>(emptyList())
    val state = _state.asStateFlow()

    // checks if the notifications are being loaded
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // store all notifications before filtering
    private val _allNotifications = MutableStateFlow<List<NotificationUi>>(emptyList())

    // holdes t he filterstate
    private val _filter = MutableStateFlow(NotificationFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _requiredUsers = MutableStateFlow<Map<Int, UserProfile>>(emptyMap())
    val highscoreEventUsers = _requiredUsers.asStateFlow()


    // JSON parser to convert backend content into JSON objects
    private val json = Json {
        ignoreUnknownKeys = true
    }

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            // set the loading state and clear previous errors
            _isLoading.update { true }
            _error.update { null }

            val requiredUserIds = mutableListOf<Int>()

            try {
                val response = notificationApi.getAllNotifications()

                // maps the backend notifications to UI models
                _allNotifications.update {
                    response.map { notification ->
                        val event = json.decodeFromString<BaseEvent>(notification.content)

                        // we only need to load the user object if the score is set by a Score & Play user
                        if (event is HighscoreEvent && event.score.sessionPlayer.guest == null) {
                            requiredUserIds.add(event.score.sessionPlayer.userId)
                        }

                        NotificationUi(
                            notificationId = notification.notificationId,
                            event = event,
                            read = notification.read
                        )
                    }.reversed() // show newest first
                }

                applyFilter()

                val foundRequiredUsers = requiredUserIds
                    .asFlow()
                    .map { userId ->
                        val foundUser = profileApi.getProfile(userId)
                        userId to foundUser
                    }
                    .toList()
                    .toMap()

                _requiredUsers.update { foundRequiredUsers }
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
                // let the backend know that the notification has been read
                notificationApi.markNotificationsAsRead(notificationId)

                // update the read state in the visible notification list
                _state.update { currentList ->
                    currentList.map { item ->
                        if (item.notificationId == notificationId)
                            item.copy(read = true)
                        else item
                    }
                }

                // update the full notification list
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