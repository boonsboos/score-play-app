package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.game.Game

class GamesListViewModel(private val gameApi: GameApi) : ViewModel() {
    private var offset = 0
    private val limit = 25;

    private val gamesSetStateFlow = MutableStateFlow(setOf<Game>())
    val gamesSet = gamesSetStateFlow.asStateFlow()

    private val loadingState = MutableStateFlow(false)
    val areLoading = loadingState.asStateFlow()

    fun fetch() {
        viewModelScope.launch {
            // indicate we are loading
            loadingState.update { true }

            val allGames = gameApi.all(offset, limit)

            gamesSetStateFlow.update {
                it union allGames.toSet()
            }

            // indicate we are no longer loading
            loadingState.update { false }
        }
    }

    fun fetchMore() {
        offset += 25
        fetch()
    }
}