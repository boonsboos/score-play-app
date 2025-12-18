package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.Game

class GamesListViewModel(private val gameApi: GameApi) : ViewModel() {
    private var offset = 0
    private val limit = 25;

    private val gamesSetStateFlow = MutableStateFlow(setOf<Game>())
    val gamesSet = gamesSetStateFlow.asStateFlow()

    private val loadingState = MutableStateFlow(false)
    val areLoading = loadingState.asStateFlow()

    suspend fun fetch() {
        // indicate we are loading
        loadingState.emit(true)

        val allGames = gameApi.all(offset, limit)

        gamesSetStateFlow.update {
            it union allGames.toSet()
        }

        // indicate we are no longer loading
        loadingState.emit(false)
    }

    suspend fun fetchMore() {
        offset += 25
        fetch()
    }
}