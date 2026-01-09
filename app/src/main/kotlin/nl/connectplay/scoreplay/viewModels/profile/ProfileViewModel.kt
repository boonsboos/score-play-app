package nl.connectplay.scoreplay.viewModels.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.models.friends.FriendshipStatus
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.models.user.UserSession
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.viewModels.UiState

class ProfileViewModel(
    private val userId: Int?,
    private val profileApi: ProfileApi,
    private val friendsApi: FriendsApi,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {
    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val profileState = _profileState.asStateFlow()

    private val _sessionsState = MutableStateFlow<UiState<List<UserSession>>>(UiState.Idle)
    val sessionsState = _sessionsState.asStateFlow()

    private val _gamesState = MutableStateFlow<UiState<List<FollowedGame>>>(UiState.Idle)
    val gamesState = _gamesState.asStateFlow()

    private val _friendshipStatus = MutableStateFlow<FriendshipStatus?>(null)
    val friendshipStatus = _friendshipStatus.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    private val _deleteAccountEvent = MutableSharedFlow<Unit>()
    val deleteAccountEvent = _deleteAccountEvent.asSharedFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        launchRequest(_profileState) {
            val profile = profileApi.getProfile(userId)

            // After we get the ID → load all dependent data
            loadLastSessions(profile.id)
            loadFollowedGames(profile.id)

            // Load Friendship Status when it's not your userId
            val myUserId = tokenDataStore.userId.firstOrNull()
            if (profile.id != myUserId) {
                loadFriendshipStatus(profile.id)
            }
            profile
        }
    }

    fun loadLastSessions(id: Int) {
        launchRequest(_sessionsState) {
            profileApi.getLastSessions(id)
        }
    }

    fun loadAllSessions(id: Int) {
        launchRequest(_sessionsState) {
            profileApi.getAllSessions(id)
        }
    }

    fun loadFollowedGames(id: Int) {
        launchRequest(_gamesState) {
            profileApi.getFollowedGames(id)
        }
    }

    // this function runs a request inside viewModelScope so you don't repeat it everywhere
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
                profileApi.deleteAccount()
                _deleteAccountEvent.emit(Unit)
            } catch (e: Exception) {
                Log.e(this::class.simpleName, "Error deleting account: ${e.message}", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearToken()
            _logoutEvent.emit(Unit)
        }
    }

    fun loadFriendshipStatus(targetUserId: Int) {
        viewModelScope.launch {
            try {
                val myUserId = tokenDataStore.userId.firstOrNull() ?: return@launch

                // Check if the target use is already a friend
                val friends = friendsApi.getFriends(myUserId)
                if (friends.any { it.user.id == targetUserId }) {
                    _friendshipStatus.update { FriendshipStatus.FRIENDS }
                    return@launch
                }

                // Check if is there is already a pending request
                val requests = friendsApi.getAllFriendRequests()
                val isPending =
                    requests.outstanding.any { it.user.id == targetUserId && it.status == FriendshipStatus.PENDING }

                // Update the friendship status based on the checks above
                _friendshipStatus.update {
                    if (isPending) FriendshipStatus.PENDING
                    else null
                }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, "Failed to load friendship status", e)
                _friendshipStatus.update { FriendshipStatus.REJECTED }
            }
        }
    }

    fun onFriendButtonClicked(targetUserId: Int) {
        when (_friendshipStatus.value) {
            FriendshipStatus.FRIENDS -> removeFriend(targetUserId)
            FriendshipStatus.REJECTED, null -> sendFriendRequest(targetUserId)
            FriendshipStatus.PENDING -> Unit
            else -> Unit
        }
    }

    private fun sendFriendRequest(targetUserId: Int) {
        viewModelScope.launch {
            try {
                friendsApi.addFriend(targetUserId)
                _friendshipStatus.update { FriendshipStatus.PENDING }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, "Failed to send friend request", e)
            }
        }
    }

    private fun removeFriend(friendId: Int) {
        viewModelScope.launch {
            try {
                friendsApi.deleteFriend(friendId)
                _friendshipStatus.update { null }
            } catch (e: Exception) {
                Log.e(this::class.simpleName, "Failed to remove friend", e)
            }
        }
    }
}