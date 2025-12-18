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
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

class SessionViewModel(
    private val sessionDao: SessionDao,
    private val sessionPlayerDao: SessionPlayerDao
): ViewModel() {
    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.Initialize -> {
                _state.update { current ->

                    // If already initialized. Do nothing.
                    if (current.userId != null) return@update current

                    val ownerPlayer = RoomSessionPlayer(
                        userId = event.userId,
                        guestName = null
                    )

                    current.copy(
                        userId = event.userId,
                        sessionPlayers = listOf(ownerPlayer)
                    )
                }
            }

            is SessionEvent.StartNewSession -> {
                viewModelScope.launch {
                    //sessionPlayerDao.deleteAllPlayers()
                    //sessionDao.deleteSession()

                    _state.value = SessionState(
                        status = SessionStatus.DRAFT
                    )
                }
            }

            SessionEvent.SaveSession -> {
                val current = _state.value
                val gameId = current.gameId
                val userId = current.userId

                if (userId == null || gameId == null) {
                    Log.d("SessionVM", "SaveSession aborted: userId=$userId, gameId=$gameId")

                    return
                }

                viewModelScope.launch {
                    // 1. Save Session
                    val session = RoomSession(
                        gameId = gameId,
                        userId = userId,
                        visibility = current.visibility
                    )

                    sessionDao.upsertSession(session)

                    // 2. Save Players
                    current.sessionPlayers.forEach { player ->
                        sessionPlayerDao.upsertSessionPlayer(player)
                    }

                    _state.update {
                        it.copy(
                            roomSession = session.copy(),
                            status = SessionStatus.SAVED
                        )
                    }
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

            is SessionEvent.AddPlayer -> {
                _state.update { current ->

                    // Check if this player already exists
                    val alreadyExists = current.sessionPlayers.any { existing ->
                        existing.userId == event.userId && existing.guestName == event.guestName
                    }

                    // If player already exists, do nothing
                    if (alreadyExists) {
                        current
                    } else {
                        // Add new player
                        current.copy(
                            sessionPlayers = current.sessionPlayers + RoomSessionPlayer(
                                userId = event.userId,
                                guestName = event.guestName
                            )
                        )
                    }
                }
            }

            is SessionEvent.RemovePlayer -> {
                _state.update { current ->
                    current.copy(
                        sessionPlayers = current.sessionPlayers.filterNot {
                            it.userId == event.userId &&
                                    it.guestName == event.guestName
                        }
                    )
                }
            }
            is SessionEvent.DeleteSessionPlayer -> TODO()
        }
    }
}