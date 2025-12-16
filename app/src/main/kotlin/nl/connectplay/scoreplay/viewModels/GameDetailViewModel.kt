package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.Game

class GameDetailUIState(
    var currentGame: Int? = null,
    var gameData: Game? = null,
    var gameFollowed: Boolean = false
)

class GameDetailViewModel(private val gameApi: GameApi): ViewModel() {
    private var _uiState = MutableStateFlow(GameDetailUIState())
    val uiState = _uiState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    suspend fun getGame(gameId: Int) {
        Log.i(this::class.simpleName, "Getting game with ID $gameId")

        _loadingState.emit(true)

        val gameObject = gameApi.single(gameId)

        _uiState.emit(
            _uiState.value.apply {
                gameData = gameObject
                currentGame = gameId
            }
        )

        _loadingState.emit(false)
    }

    suspend fun toggleFollow() {
        _uiState.emit(_uiState.value.apply { gameFollowed = !gameFollowed })

        // TODO: call follow endpoint
    }
}