package nl.connectplay.scoreplay.viewModels

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.Game

class GameDetailUIState(var currentGame: Int? = null, var gameData: Game? = null, var images: List<ImageVector> = listOf())


class GameDetailViewModel(private val gameApi: GameApi): ViewModel() {

    // TODO: import Coil to download the images
    private var _uiState = MutableStateFlow(GameDetailUIState())
    val uiState = _uiState.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    suspend fun getGame(gameId: Int) {
        _loadingState.emit(true)

        val gameData = gameApi.single(gameId)

        _uiState.emit(GameDetailUIState(currentGame = gameId, gameData = gameData))

        _loadingState.emit(false)
    }
}