package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.Game

class GameDetailUIState(
    var currentGame: Int? = null,
    var gameData: Game? = null,
    var imageUrls: List<String> = listOf(),
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

        val gameData = gameApi.single(gameId)
        val gamePictures = gameApi.pictures(gameId)

        _uiState.update { state ->
            state.currentGame = gameId
            state.gameData = gameData
            state.imageUrls = gamePictures

            state
        }

        _loadingState.emit(false)
    }

    suspend fun toggleFollow() {
        _uiState.emit(_uiState.value.apply { gameFollowed = !gameFollowed })

        // TODO: call follow endpoint
    }
}