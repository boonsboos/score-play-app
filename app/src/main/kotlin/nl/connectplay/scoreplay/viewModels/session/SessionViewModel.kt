package nl.connectplay.scoreplay.viewModels.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
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
import nl.connectplay.scoreplay.models.session.CreateSessionRequest
import nl.connectplay.scoreplay.models.session.CreateSessionScoreRequest
import nl.connectplay.scoreplay.models.session.SessionPlayerDto
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
    private val friendsApi: FriendsApi,
    private val sessionApi: SessionApi,
    private val tokenDataStore: TokenDataStore
): ViewModel() {
    private val _state = MutableStateFlow(SessionState())

    val state = _state.asStateFlow()

    private var observeTurnsJob: Job? = null

    private val _games = MutableStateFlow<List<Game>>(emptyList())

    val games: StateFlow<List<Game>> = _games

    private val _friends = MutableStateFlow<List<UserFriend>>(emptyList())

    val friends: StateFlow<List<UserFriend>> = _friends

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private suspend fun getUserId(): Int? {
        return tokenDataStore.userId.firstOrNull()
    }

    private fun freshState(userId: Int): SessionState {
        val ownerPlayer = RoomSessionPlayer(
            userId = userId,
            guestName = null
        )

        return SessionState(
            status = SessionStatus.DRAFT,
            roomSession = null,
            gameId = null,
            userId = userId,
            visibility = SessionVisibility.ANONYMISED,
            sessionPlayers = listOf(ownerPlayer),
            scores = emptyList(),
            turns = emptyList(),
            winnerPlayer = null,
            winnerScore = null
        )
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

    fun loadActiveSessionFromDb(computeWinner: Boolean = false) {
        viewModelScope.launch {
            val session = sessionDao.getSession()
            val players = sessionPlayerDao.getSessionPlayers()

            val winner = if (computeWinner) {
                val scores = sessionScoreDao.getScoresForSession()
                winnerHighestTotal(players, scores)
            } else null

            _state.update {
                it.copy(
                    roomSession = session,
                    gameId = session.gameId,
                    userId = session.userId,
                    sessionPlayers = players,
                    status = SessionStatus.SAVED,
                    winnerPlayer = winner?.player,
                    winnerScore = winner?.totalScore
                )
            }

            // Observe rounds list
            observeTurnsJob?.cancel()
            observeTurnsJob = viewModelScope.launch {
                sessionScoreDao.observeTurns(session.id).collect { turns ->
                    _state.update { it.copy(turns = turns) }
                }
            }
        }
    }

    fun onEvent(event: SessionEvent) {
        when(event) {
            is SessionEvent.Initialize -> {
                _state.update { current ->
                    val userId = event.userId

                    val hasOwnerAlready = current.sessionPlayers.any {
                        it.userId == userId && it.guestName == null
                    }

                    when (current.userId) {
                        null -> freshState(userId)
                        userId if !hasOwnerAlready -> current.copy(
                            sessionPlayers = listOf(
                                RoomSessionPlayer(userId = userId, guestName = null)
                            )
                        )
                        else -> current
                    }
                }
            }

            is SessionEvent.StartNewSession -> {
                viewModelScope.launch {
                    val userId = _state.value.userId ?: getUserId() ?: return@launch

                    // Stop old observers
                    observeTurnsJob?.cancel()
                    observeTurnsJob = null

                    // Reset UI state
                    _state.value = freshState(userId)

                    // Empty Room Database
                    sessionScoreDao.deleteAllScores()
                    sessionPlayerDao.deleteAllPlayers()
                    sessionDao.deleteSession()

                    _state.value = freshState(userId)

                    Log.d(
                        "SessionVM","""
                            |SessionState {
                            |  status=${_state.value.status}
                            |  roomSession=${_state.value.roomSession}
                            |  gameId=${_state.value.gameId}
                            |  userId=${_state.value.userId}
                            |  visibility=${_state.value.visibility}
                            |  sessionPlayers(${_state.value.sessionPlayers.size})=${_state.value.sessionPlayers}
                            |  turns(${_state.value.turns.size})=${_state.value.turns}
                            |  winnerPlayer=${_state.value.winnerPlayer}
                            |  winnerScore=${_state.value.winnerScore}
                            |}
                        """.trimMargin()
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
                    val session = RoomSession(
                        gameId = gameId,
                        userId = userId,
                        visibility = current.visibility
                    )

                    /** 1. Save Session to Room Database */
                    sessionDao.upsertSession(session)

                    /** 2. Save Players to Room Database */
                    current.sessionPlayers.forEach { player ->
                        sessionPlayerDao.upsertSessionPlayer(player)
                    }

                    /** 3. Read back the saved entities from Room Database */
                    val savedSession = sessionDao.getSession()
                    val savedPlayers = sessionPlayerDao.getSessionPlayers()

                    _state.update {
                        it.copy(
                            roomSession = savedSession,
                            sessionPlayers = savedPlayers,
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
                /** 1. Update UI State */
                _state.update { current ->
                    current.copy(
                        sessionPlayers = current.sessionPlayers.filterNot {
                            it.userId == event.userId && it.guestName == event.guestName
                        }
                    )
                }

                /** 2. Persist delete to Room Database */
                viewModelScope.launch {
                    try {
                        sessionPlayerDao.deleteSessionPlayer(event.userId, event.guestName)
                    } catch (e: Exception) {
                        Log.e("SessionVM", "Failed to delete player from DB", e)
                        loadActiveSessionFromDb()
                    }
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

            SessionEvent.FinishSession -> {
                viewModelScope.launch {
                    try {
                        val session = sessionDao.getSession()
                        val players = sessionPlayerDao.getSessionPlayers()
                        val scores = sessionScoreDao.getScoresForSession()

                        /** 1. Create Session on backend */
                        val createResp = sessionApi.createSession(
                            CreateSessionRequest(
                                gameId = session.gameId,
                                userId = session.userId,
                                visibility = session.visibility.toInt()
                            )
                        )

                        // Backend-generated session UUID (used for subsequent uploads)
                        val remoteSessionId = createResp.sessionId

                        // Map local playerId -> player
                        val playersById = players.associateBy { it.sessionPlayerId }

                        /** 2. Add all Scores on backend */
                        val payload: List<CreateSessionScoreRequest> = scores.map { s ->
                            val p = playersById[s.sessionPlayerId]
                                ?: error("No player found for sessionPlayerId=${s.sessionPlayerId}")

                            CreateSessionScoreRequest(
                                score = s.score,
                                turn = s.turn,
                                sessionPlayer = SessionPlayerDto(
                                    userId = p.userId,
                                    guest = p.guestName
                                )
                            )
                        }

                        /** 3. Bulk upload scores (incl. players) */
                        sessionApi.addScores(remoteSessionId, payload)

                    } catch (e: Exception) {
                        Log.e("SessionVM", "FinishSession failed", e)
                    }
                }
            }
        }
    }
}