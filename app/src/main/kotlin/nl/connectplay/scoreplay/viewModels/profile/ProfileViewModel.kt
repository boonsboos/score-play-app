package nl.connectplay.scoreplay.viewModels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.models.user.UserSession
import nl.connectplay.scoreplay.stores.TokenDataStore

class ProfileViewModel(private val userId: Int?, private val profileApi: ProfileApi, private val tokenDataStore: TokenDataStore) : ViewModel() {
    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Loading)
    val profileState = _profileState.asStateFlow()
    private val _sessionsState = MutableStateFlow<UiState<List<UserSession>>>(UiState.Loading)
    val sessionsState = _sessionsState.asStateFlow()
    private val _gamesState = MutableStateFlow<UiState<List<Game>>>(UiState.Loading)
    val gamesState = _gamesState.asStateFlow()
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent
    private val _deleteAccountEvent = MutableSharedFlow<Unit>()
    val deleteAccountEvent: SharedFlow<Unit> = _deleteAccountEvent

    init {
        loadProfile()
    }

    fun loadProfile() {
        launchRequest(_profileState) {
            val profile = profileApi.getProfile(userId)

            // After we get the ID → load all dependent data
            loadLastSessions(profile.id)
            loadFollowedGames(profile.id)

            profile
        }
    }

    fun loadLastSessions(id: Int) {
        launchRequest(_sessionsState) {
            profileApi.getLastSessions(id)
        }
    }

    fun loadFollowedGames(id: Int) {
        launchRequest(_gamesState) {
            profileApi.getFollowedGames(id)
        }
    }

    private fun <T> launchRequest(
        state: MutableStateFlow<UiState<T>>,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            state.value = UiState.Loading
            try {
                val result = block()
                state.value = UiState.Success(result)
            } catch (e: Exception) {
                state.value = UiState.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                println("Calling API to delete account")
                profileApi.deleteAccount()
                println("Account deleted successfully")
                _deleteAccountEvent.emit(Unit)
            } catch (e: InvalidTokenException) {
                println("Token invalid: ${e.message}")
            } catch (e: Exception) {
                println("Error deleting account: ${e.message}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearToken()
            _logoutEvent.emit(Unit)
        }
    }

}