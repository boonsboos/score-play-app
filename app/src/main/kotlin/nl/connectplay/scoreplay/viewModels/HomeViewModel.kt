package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.stores.TokenDataStore

class HomeViewModel(
    private val profileApi: ProfileApi,
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {
    private val userId = tokenDataStore.userId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )
    private val _recentState = MutableStateFlow<UiState<List<Game>>>(UiState.Idle)
    val recentState = _recentState.asStateFlow()
    private val _followedState = MutableStateFlow<UiState<List<FollowedGame>>>(UiState.Idle)
    val followedState = _followedState.asStateFlow()

    init {
        observeUserId()
    }

    private fun observeUserId() {
        userId
            .onEach { id ->
                if (id == null) {
                    _followedState.value = UiState.Error(
                        "Invalid or expired token",
                        InvalidTokenException("Invalid or expired token")
                    )
                } else {
                    loadRecentGames(id)
                    loadFollowedGames(id)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadRecentGames(userId: Int) {
        viewModelScope.launch {
            _followedState.value = UiState.Loading
            try {
                val result = profileApi.getRecentGames(userId, true)
                _followedState.value = UiState.Success(result)
            } catch (e: Exception) {
                _followedState.value = UiState.Error(e.message ?: "Unknown error", e)
            }
        }
    }
    private fun loadFollowedGames(userId: Int) {
        viewModelScope.launch {
            _followedState.value = UiState.Loading
            try {
                val result = profileApi.getFollowedGames(userId, true)
                _followedState.value = UiState.Success(result)
            } catch (e: Exception) {
                _followedState.value = UiState.Error(e.message ?: "Unknown error", e)
            }
        }
    }
}