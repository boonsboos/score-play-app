package nl.connectplay.scoreplay.viewModels.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.models.session.Session

class SessionDetailViewModel(
    private val sessionApi: SessionApi
) : ViewModel() {

    private val _state = MutableStateFlow(SessionDetailState())
    val state = _state.asStateFlow()

    fun loadSession(targetId: Int, sessionId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val session: Session = sessionApi.loadSessionById(targetId, sessionId)

                _state.update {
                    it.copy(
                        isLoading = false,
                        session = session
                    )
                }
            } catch (e: Exception) {
                Log.e("SessionDetailVM", "Failed to load session", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load session"
                    )
                }
            }
        }
    }

    fun deleteSession() {
        val sessionId = state.value.session?.sessionId
        if (sessionId.isNullOrEmpty()) {
            Log.w("SessionDetailVM", "No session to delete")
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                sessionApi.deleteSessionById(sessionId)

                _state.update { SessionDetailState() }

                Log.d("SessionDetailVM", "Session deleted successfully")
            } catch (e: Exception) {
                Log.e("SessionDetailVM", "Failed to delete session $sessionId", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to delete session"
                    )
                }
            }
        }
    }
}
