package nl.connectplay.scoreplay.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.LeaderboardApi
import nl.connectplay.scoreplay.models.leaderboard.LeaderboardEntry

class LeaderboardViewModel(private val gameId: Int, private val leaderboardApi: LeaderboardApi): ViewModel() {

    private val LOG_TAG = this::class.simpleName

    private val _scores = MutableStateFlow<List<LeaderboardEntry>>(listOf())
    val scores = _scores.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    init {
        fetch()
    }

    fun fetch() = viewModelScope.launch {
        try {
            _loading.update { true }

            _scores.update { leaderboardApi.getScoresFor(gameId) }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to fetch leaderboard scores for game $gameId: ${e.message}", e)
        } finally {
            _loading.update { false }
        }
    }
}