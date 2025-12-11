package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.NotificationApi
import nl.connectplay.scoreplay.models.notifications.Notification
import nl.connectplay.scoreplay.models.notifications.NotificationFilter

class NotificationListViewModel(private val notificationApi: NotificationApi) : ViewModel() {
    private val _state = MutableStateFlow<List<Notification>>(emptyList())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _allNotifications = MutableStateFlow<List<Notification>>(emptyList())

    private val _filter = MutableStateFlow(NotificationFilter.ALL)
    val filter = _filter.asStateFlow()


    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.update { true }
            _error.update { null }
            try {
                val response = notificationApi.getAllNotifications()
                _allNotifications.value = response
                _state.value = response
                applyFilter()
            } catch (e: Exception) {
                e.printStackTrace()
                _error.update { e.message ?: "An unexpected error occurred" }
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
            NotificationFilter.FRIEND_REQUEST -> allNotifications.filter { it.content.contains("friend", ignoreCase = true) }
            NotificationFilter.HIGHSCORES -> allNotifications.filter { it.content.contains("highscore", ignoreCase = true)}
        }
        _state.value = filtered
    }

    fun markNotificationsAsRead(notification: Notification) {
        viewModelScope.launch {
            try {
                notificationApi.markNotificationsAsRead(notification.notificationId) // send the patch request to the api to mark as read
                // we get the currentList of notifications
                _state.update { currentList ->
                    currentList.map { notificationItem -> // loop over the current notificationItem to check witch is clicked one
                        if (notificationItem.notificationId == notification.notificationId)
                            notificationItem.copy(read = true) // mark clicked notificationItem as read
                        else notificationItem // keep original state
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Failed to update notification"
            }
        }
    }
}