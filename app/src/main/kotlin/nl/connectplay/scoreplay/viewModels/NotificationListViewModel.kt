package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.NotificationApi
import nl.connectplay.scoreplay.models.Notification

class NotificationListViewModel(private val notificationApi: NotificationApi) : ViewModel() {
    private val _state = MutableStateFlow<List<Notification>>(emptyList())
    val state = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val response = notificationApi.getAllNotifications()
                _state.update { response }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.update { e.message ?: "An unexpected error occurred" }
            } finally {
                _isLoading.update { false }
            }
        }
    }
}