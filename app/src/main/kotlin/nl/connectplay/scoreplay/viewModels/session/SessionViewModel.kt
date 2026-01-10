package nl.connectplay.scoreplay.viewModels.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.models.dto.SessionPlayerDto
import nl.connectplay.scoreplay.models.friends.UserFriend
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.dto.CreateSessionDto
import nl.connectplay.scoreplay.models.dto.CreateScoreDto
import nl.connectplay.scoreplay.models.dto.UpdateSessionDto
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.room.dao.SessionDao
import nl.connectplay.scoreplay.room.dao.SessionPlayerDao
import nl.connectplay.scoreplay.room.dao.SessionScoreDao
import nl.connectplay.scoreplay.room.entities.RoomSession
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore
import nl.connectplay.scoreplay.stores.TokenDataStore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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

    // NOTE: Shared loading flag for multiple startup fetches; may toggle twice during init.
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // Snackbar for success message
    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()

    private val _sessionEndImage = MutableStateFlow<SessionEndImageState?>(null)
    val sessionEndImage = _sessionEndImage.asStateFlow()

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
        _loading.update { true }
        fetchGames()
        fetchFriends()
        _loading.update { false }
    }

    private fun fetchGames() {
        viewModelScope.launch {
            if (_games.value.isNotEmpty()) return@launch

            try {
                val games = gameApi.all()
                _games.update { games }
            } catch (e: Exception) {
                Log.e("SessionViewModel", "Failed to fetch games", e)
            }
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            if (_friends.value.isNotEmpty()) return@launch

            val userId = getUserId() ?: return@launch

            try {
                val friends = friendsApi.getFriends(userId)
                _friends.update { friends }
            } catch (e: Exception) {
                Log.e("SessionViewModel", "Failed to fetch friends", e)
            }
        }
    }

    fun loadActiveSessionFromDb(computeWinner: Boolean = false) {
        viewModelScope.launch {
            val session = sessionDao.getSession()
            val players = sessionPlayerDao.getSessionPlayers()

            /**
             * Optional winner computation.
             * - Only calculate when needed (e.g. on a "Finish" / summary screen) to avoid extra I/O.
             * - Requires pulling all scores and knowing the game's scoring method.
             */
            val winner = if (computeWinner) {
                // Fetch game meta to determine scoring rule (e.g. lowest total wins vs highest total wins).
                val game = gameApi.single(session.gameId)
                val scoringMethod: Int = game?.scoringMethodId ?: 0

                val scores = sessionScoreDao.getScoresForSession()

                if (scoringMethod == 2) {
                    winnerLowestTotal(players, scores)
                } else {
                    winnerHighestTotal(players, scores)
                }
            } else null

            /**
             * Update UI state with the latest persisted session snapshot.
             * We set status=SAVED because the state now reflects Room (not just in-memory draft).
             */
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

            /**
             * Keep the "turns" list live.
             * When the screen is re-entered or this function is called again, we must cancel the
             * previous collector to avoid multiple active collectors updating state concurrently
             * (memory leak + duplicated updates).
             */
            observeTurnsJob?.cancel()

            /**
             * Start collecting turns for the active session.
             * observeTurns(session.id) should emit whenever scores/turns change in Room, allowing
             * the UI to react immediately (Compose state-driven UI).
             */
            observeTurnsJob = viewModelScope.launch {
                sessionScoreDao.observeTurns(session.id).collect { turns ->
                    _state.update { it.copy(turns = turns) }
                }
            }
        }
    }

    fun onEvent(event: SessionEvent) {
        when (event) {
            is SessionEvent.Initialize       -> handleInitialize(event)
            is SessionEvent.StartNewSession  -> handleStartNewSession()
            SessionEvent.SaveSession         -> handleSaveSession()
            is SessionEvent.SetGame          -> handleSetGame(event)
            is SessionEvent.SetVisibility    -> handleSetVisibility(event)
            is SessionEvent.UpdateVisibility -> handleUpdateVisibility(event)
            is SessionEvent.AddPlayer        -> handleAddPlayer(event)
            is SessionEvent.RemovePlayer     -> handleRemovePlayer(event)
            is SessionEvent.AddRound         -> handleAddRound(event)
            SessionEvent.FinishSession       -> handleFinishSession()
        }
    }

    private fun handleInitialize(event: SessionEvent.Initialize) {
        /** Ensure the logged-in user is always present as the owner player (guestName == null) and never duplicated. */
        _state.update { current ->
            val userId = event.userId
            val hasOwnerAlready = current.sessionPlayers.any { it.userId == userId && it.guestName == null }

            when (current.userId) {
                null -> freshState(userId)
                userId if !hasOwnerAlready -> current.copy(
                    sessionPlayers = listOf(RoomSessionPlayer(userId = userId, guestName = null))
                )
                else -> current
            }
        }
    }

    private fun handleStartNewSession() {
        viewModelScope.launch {
            val userId = _state.value.userId ?: getUserId() ?: return@launch

            // 1. Stop DB collectors to avoid receiving updates while wiping local data.
            observeTurnsJob?.cancel()
            observeTurnsJob = null

            // 2. Reset UI immediately so the user sees a clean draft state while Room is cleared.
            _state.value = freshState(userId)

            // 3. Clear local "active session" storage (this app keeps only one active session locally).
            sessionScoreDao.deleteAllScores()
            sessionPlayerDao.deleteAllPlayers()
            sessionDao.deleteSession()

            // 4. Re-apply draft state after cleanup to guarantee a consistent baseline.
            _state.value = freshState(userId)
        }
    }

    private fun handleSaveSession() {
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

            /** 3. Read back from Room to pick up generated IDs / defaults and keep UI state in sync with persisted state. */
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

    private fun handleSetGame(e: SessionEvent.SetGame) {
        _state.update { it.copy(
            gameId = e.gameId
        ) }
    }
    private fun handleSetVisibility(e: SessionEvent.SetVisibility) {
        _state.update { it.copy(
            visibility = e.visibility
        ) }
    }
    private fun handleUpdateVisibility(e: SessionEvent.UpdateVisibility) {
        viewModelScope.launch {
            sessionDao.updateVisibility(e.visibility)

            // optional: optimistic UI update if your DB flow isn’t immediate
            _state.update { it.copy(visibility = e.visibility) }
        }
    }

    private fun handleAddPlayer(e: SessionEvent.AddPlayer) {
        _state.update { current ->
            // Check if this player already exists
            val alreadyExists = current.sessionPlayers.any { existing ->
                existing.userId == e.userId && existing.guestName == e.guestName
            }

            // If player already exists, do nothing
            if (alreadyExists) {
                current
            } else {
                // Add new player
                current.copy(
                    sessionPlayers = current.sessionPlayers + RoomSessionPlayer(
                        userId = e.userId,
                        guestName = e.guestName
                    )
                )
            }
        }
    }

    private fun handleRemovePlayer(e: SessionEvent.RemovePlayer) {
        /** 1. Update UI State */
        _state.update { current ->
            current.copy(
                sessionPlayers = current.sessionPlayers.filterNot {
                    it.userId == e.userId && it.guestName == e.guestName
                }
            )
        }

        /** 2. Persist delete to Room Database */
        viewModelScope.launch {
            try {
                sessionPlayerDao.deleteSessionPlayer(e.userId, e.guestName)
            } catch (e: Exception) {
                Log.e("SessionVM", "Failed to delete player from DB", e)
                loadActiveSessionFromDb()
            }
        }
    }

    private fun handleAddRound(e: SessionEvent.AddRound) {
        viewModelScope.launch {
            val nextTurn = (sessionScoreDao.getMaxTurn(e.sessionId) ?: 0) + 1

            val entities = e.scores.map { input ->
                RoomSessionScore(
                    sessionId = e.sessionId,
                    sessionPlayerId = input.sessionPlayerId,
                    gameId = e.gameId,
                    score = input.score,
                    turn = nextTurn
                )
            }

            sessionScoreDao.insertAll(entities)

            // If DB delete fails, reload from Room to restore the single source of truth.
            loadActiveSessionFromDb()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun handleFinishSession() {
        viewModelScope.launch {
            try {
                val session = sessionDao.getSession()

                /** 1) Create a session on the backend and obtain the backend session identifier (UUID/String). */
                val createResp = sessionApi.createSession(
                    CreateSessionDto(
                        gameId = session.gameId,
                        visibility = session.visibility.toInt()
                    )
                )

                // If createResp is a DTO: use createResp.sessionId. If it already is a String, rename createResp -> remoteSessionId.
                val remoteSessionId: String = createResp

                /** 2) Build bulk payload of all persisted turns for upload. */
                val scores = prepareScores()

                /** 3) Upload all scores in one call. */
                sessionApi.addScores(remoteSessionId, scores)
                Log.d("SessionVM", "addScores OK -> uploaded=${scores.size}")

                // 4) Set the end time
                sessionApi.update(remoteSessionId, updateSessionDto = UpdateSessionDto(endTime = Clock.System.now().toString()))

                // 5) optionally upload the picture
                uploadSessionEndImage(remoteSessionId)

                _snackbar.tryEmit("Session uploaded successfully!")

            } catch (e: Exception) {
                Log.e("SessionVM", "FinishSession failed", e)
            }
        }
    }

    private suspend fun uploadSessionEndImage(remoteSessionId: String): Boolean {
        // only upload if the user selected an image
        val sessionImageUploadSuccess = _sessionEndImage.value?.let {
            val inputStream = it.context.contentResolver?.openInputStream(
                it.image
            )

            // make sure the input stream is closed afterwards
            // we cannot rely on Ktor to close it for us
            inputStream?.use { imageData ->
                sessionApi.addEndPicture(
                    sessionId = remoteSessionId,
                    dataInputStream = imageData
                )
            }
        } ?: false

        // release our reference to the Context
        // resetting the replay cache on the MutableStateFlow doesn't do anything so let's just emit null.
        _sessionEndImage.emit(null)

        return sessionImageUploadSuccess
    }

    suspend fun prepareScores(): List<CreateScoreDto> {
        val players = sessionPlayerDao.getSessionPlayers()
        val scores = sessionScoreDao.getScoresForSession()
        /**
         * Room scores reference players by local sessionPlayerId.
         * Backend expects player identity by (userId, guestName), so we map local IDs back to player info.
         */
        val playersById = players.associateBy { it.sessionPlayerId }

        Log.d("prepareScores", "${scores.size}")

        val payload: MutableList<CreateScoreDto> = mutableListOf()
        for (score in scores) {
            val player = playersById[score.sessionPlayerId]

            if (player == null) {
                Log.e(
                    "FinishSession",
                    "Skipping score: no player for sessionPlayerId=${score.sessionPlayerId}, scoreId=${score.id}"
                )
                continue
            }

            payload.add(
                CreateScoreDto(
                    score = score.score,
                    turn = score.turn,
                    sessionPlayer = SessionPlayerDto(
                        userId = player.userId,
                        guest = player.guestName
                    )
                )
            )
        }
        return payload
    }

    fun addImage(sessionEndImageState: SessionEndImageState) {
        _sessionEndImage.value = sessionEndImageState
    }
}