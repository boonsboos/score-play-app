package nl.connectplay.scoreplay.viewModels.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.friends.UserFriend
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.dao.SessionScoreDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore
import nl.connectplay.scoreplay.stores.TokenDataStore

class SessionViewModel(
    private val sessionDao: SessionDao,
    private val sessionPlayerDao: SessionPlayerDao,
    private val sessionScoreDao: SessionScoreDao,
    private val gameApi: GameApi,
    private val sessionApi: SessionApi,
    private val friendsApi: FriendsApi,
    private val tokenDataStore: TokenDataStore
): ViewModel() {
    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    private val _games = MutableStateFlow<List<Game>>(emptyList())

    val games: StateFlow<List<Game>> = _games

    private val _friends = MutableStateFlow<List<UserFriend>>(emptyList())

    val friends: StateFlow<List<UserFriend>> = _friends

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    private suspend fun getUserId(): Int? {
        return tokenDataStore.userId.firstOrNull()

    }

    init {
        fetchGames()
        fetchFriends()

        viewModelScope.launch {
            friends.collect { list ->
                Log.d("SessionVM", "friends updated: size=${list.size} -> $list")
            }
        }

        viewModelScope.launch {
            games.collect { list ->
                Log.d("SessionVM", "games updated: size=${list.size} -> $list")
            }
        }
    }

    private fun fetchGames() {
        viewModelScope.launch {
            if (_games.value.isNotEmpty()) return@launch

            _loading.update { true }

            try {
                val games = gameApi.all()
                _games.update { games }
            } catch (e: Exception) {
                Log.e("SessionViewModel", "Failed to fetch games", e)
            } finally {
                _loading.update { false }
            }
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            if (_friends.value.isNotEmpty()) return@launch

            val userId = getUserId() ?: return@launch

            _loading.update { true }

            try {
                val friends = friendsApi.getFriends(userId)
                _friends.update { friends }
            } catch (e: Exception) {
                Log.e("SessionViewModel", "Failed to fetch friends", e)
            } finally {
                _loading.update { false }
            }
        }
    }

    fun loadActiveSessionFromDb() {
        viewModelScope.launch {
            val session = sessionDao.getSession()
            val players: List<RoomSessionPlayer> = sessionPlayerDao.getSessionPlayers()

            _state.update {
                it.copy(
                    roomSession = session,
                    gameId = session.gameId,
                    userId = session.userId,
                    sessionPlayers = players,
                    status = SessionStatus.SAVED
                )
            }

            // Observe rounds list
            sessionScoreDao.observeTurns(session.id).collect { turns ->
                _state.update { it.copy(turns = turns) }
            }
        }
    }

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.Initialize -> {
                _state.update { current ->

                    // If already initialized. Do nothing.
                    if (current.userId != null) return@update current

                    val ownerPlayer = RoomSessionPlayer(
                        userId = event.userId,
                        guestName = null,
                    )

                    current.copy(
                        userId = event.userId,
                        sessionPlayers = listOf(ownerPlayer)
                    )
                }
            }

            is SessionEvent.StartNewSession -> {
                viewModelScope.launch {
                    sessionPlayerDao.deleteAllPlayers()
                    sessionDao.deleteSession()

                    val currentUserId = _state.value.userId
                    _state.value = SessionState(
                        status = SessionStatus.DRAFT,
                        userId = currentUserId,
                        session = null,
                        roomSession = null,
                        sessionPlayers = emptyList(),
                        scores = emptyList(),
                        turns = emptyList(),
                        sessionId = null,
                        gameId = null,
                        visibility = SessionVisibility.ANONYMISED
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
                                guestName = event.guestName,
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

            is SessionEvent.DeleteSessionPlayer -> {
                Log.w("SessionVM", "DeleteSessionPlayer event received but not implemented; ignoring.")
            }

            is SessionEvent.AddRound -> {
                viewModelScope.launch {
                    val nextTurn = (sessionScoreDao.getMaxTurn(event.sessionId) ?: 0) + 1

                    val entities = event.scores.map { input ->
                        RoomSessionScore(
                            sessionId = event.sessionId,
                            sessionPlayerId = input.sessionPlayerId,
                            gameId = event.gameId,
                            score = input.score,
                            turn = nextTurn
                        )
                    }

                    sessionScoreDao.insertAll(entities)

                    // refresh state
                    loadActiveSessionFromDb()
                }
            }
        }
    }
}