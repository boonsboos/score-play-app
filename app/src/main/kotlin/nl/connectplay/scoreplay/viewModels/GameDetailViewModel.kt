package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.GameDetailDto

class GameDetailViewModel(private val gameApi: GameApi): ViewModel() {
    private val _gameState = MutableStateFlow<GameDetailDto?>(null)
    val gameState = _gameState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    suspend fun getGame(gameId: Int) = coroutineScope {
        Log.i(this::class.simpleName, "Getting game with ID $gameId")

        _loadingState.update { true }

        val gameObject = gameApi.single(gameId)
        _gameState.update { gameObject }

        _loadingState.update { false }
    }

    suspend fun toggleFollow(gameId: Int): Boolean {
        val gameDetailCopy = _gameState.value ?: return false

        val success = if (gameDetailCopy.following) {
            gameApi.unfollow(gameId)
        } else {
            gameApi.follow(gameId)
        }

        if (success) {
            _gameState.update { it?.copy(following = !it.following) }
        }

        return success
    }
}