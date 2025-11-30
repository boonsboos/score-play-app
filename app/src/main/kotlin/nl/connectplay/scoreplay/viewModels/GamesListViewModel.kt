package nl.connectplay.scoreplay.viewModels

import android.R
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import nl.connectplay.scoreplay.api.GameApi
import nl.connectplay.scoreplay.models.Game

class GamesListViewModel(private val gameApi: GameApi) : ViewModel() {
    private var offset = 0
    private val limit = 25;

    private val gamesListStateFlow = MutableStateFlow(listOf<Game>())

    val gamesList = gamesListStateFlow.asStateFlow()

    private val loadingState = MutableStateFlow(false)
    val areLoading = loadingState.asStateFlow()

    suspend fun fetch() {
        // indicate we are loading
        loadingState.emit(true)

        val allGames = gameApi.all(offset, limit)

        gamesListStateFlow.emit(
            gamesList.value + allGames
        )

        // indicate we are no longer loading
        loadingState.emit(false)
    }

    suspend fun fetchMore() {
        offset += 25
        fetch()
    }
}