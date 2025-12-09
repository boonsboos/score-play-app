package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.SessionEvent
import nl.connectplay.scoreplay.dao.SessionDao
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.session.Session

data class SessionState(
    val session: Session? = null,
    val gameId: String = "",
    val userId: String = "",
    val visibility: SessionVisibility = SessionVisibility.ANONYMISED,
    val isOnSetup: Boolean = true,
)

class SessionViewModel(
    private val dao: SessionDao
): ViewModel() {

    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.DeleteSession -> {
                viewModelScope.launch {
                    dao.deleteSession(event.session)
                }
            }
            SessionEvent.SaveSession -> {
                val gameId = _state.value.gameId
                val userId = _state.value.userId
                val visibility = _state.value.visibility

                if (gameId.isBlank() || userId.isBlank()) {
                    return
                }

                val session = Session(
                    gameId = gameId,
                    userId = userId,
                    visibility = visibility
                )

                viewModelScope.launch {
                    dao.upsertSession(session)
                }
            }
            is SessionEvent.SetGame -> {
                _state.update { it.copy(
                    gameId = event.gameId
                ) }
            }
            is SessionEvent.SetVisibility -> {
                _state.update { it.copy(
                    visibility = event.visibility
                ) }
            }

            SessionEvent.showSetup -> {
                _state.update { it.copy(
                    isOnSetup = true
                ) }
            }

            SessionEvent.showScores -> {
                _state.update { it.copy(
                    isOnSetup = false
                ) }
            }

        }

    }
}