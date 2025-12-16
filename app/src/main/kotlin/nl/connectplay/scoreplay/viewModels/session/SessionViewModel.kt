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
import nl.connectplay.scoreplay.screens.Screens

class SessionViewModel(
    private val sessionDao: SessionDao,
    private val sessionPlayerDao: SessionPlayerDao
): ViewModel() {

    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.DeleteSession -> {
                viewModelScope.launch {
                    sessionDao.deleteSession(event.roomSession)
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
                    sessionDao.upsertSession(
                        RoomSession(
                            gameId = gameId,
                            userId = userId,
                            visibility = current.visibility
                        )
                    )

                    // 2. Save Players
                    current.sessionPlayers.forEach { player ->
                        sessionPlayerDao.upsertSessionPlayer(player)
                    }
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

            is SessionEvent.AddPlayer -> {
                _state.update { current ->
                    val ownerId = current.userId ?: return@update current

                    // Check if this player already exists
                    val alreadyExists = current.sessionPlayers.any { existing ->
                        when {
                            // Owner: can only be added once
                            event.guestName == null && event.userId == ownerId ->
                                existing.userId == ownerId && existing.guestName == null

                            // Friend: unique per userId
                            event.guestName == null ->
                                existing.userId == event.userId && existing.guestName == null

                            // Guest: guest names must be unique
                            else ->
                                existing.guestName == event.guestName
                        }
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