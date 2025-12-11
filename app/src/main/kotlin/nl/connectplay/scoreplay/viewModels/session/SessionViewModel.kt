package nl.connectplay.scoreplay.viewModels.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.entities.RoomSession

class SessionViewModel(
    private val dao: SessionDao
): ViewModel() {

    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.DeleteSession -> {
                viewModelScope.launch {
                    dao.deleteSession(event.roomSession)
                }
            }

            SessionEvent.SaveSession -> {
                val current = _state.value
                val gameId = current.gameId
                val userId = current.userId
                val visibility = current.visibility

                if (userId == null || gameId == null) {
                    Log.d("SessionVM", "SaveSession aborted: userId=$userId, gameId=$gameId")

                    return
                }

                val roomSession = RoomSession(
                    gameId = gameId,
                    userId = userId,
                    visibility = visibility
                )

                viewModelScope.launch {
                    dao.upsertSession(roomSession)
                }
            }

            is SessionEvent.SetUser -> {
                _state.update { it.copy(
                    userId = event.userId
                ) }
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

            SessionEvent.ShowSetup -> {
                _state.update { it.copy(
                    isOnSetup = true
                ) }
            }

            SessionEvent.ShowScores -> {
                _state.update { it.copy(
                    isOnSetup = false
                ) }
            }
        }
    }
}