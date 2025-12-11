package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendRequestApi
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendRequest
import nl.connectplay.scoreplay.stores.TokenDataStore

// UI state for the FriendList screen
data class FriendsUiState(
    val friendRequests: List<FriendRequest> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FriendViewModel(
    private val friendsApi: FriendsApi,
    private val friendRequestApi: FriendRequestApi,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    // Backing property for UI state
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    // Helper function to get the current userId from DataStore
    private suspend fun getUserId(): Int? {
        return tokenDataStore.userId.firstOrNull()
    }

    // Initialize the ViewModel by loading the friend data
    init {
        refreshData()
    }

    /**
     * Refreshes the friends and friend requests data for the current user.
     * Updates the UI state accordingly.
     */
    fun refreshData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val userId = getUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "User not logged in") }
                return@launch
            }

            try {
                // Fetch friends and friend requests from the API
                val friends = friendsApi.getFriendsById(userId)
                friends.forEach { println("DEBUG ${it.username} is ${it.status}") }
                val requests = friendRequestApi.getAllFriendrequests(userId)
                requests.forEach { println("DEBUG ${it.username}") }

                _uiState.update {
                    it.copy(
                        friends = friends,
                        friendRequests = requests,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Failed to fetch friends")
                }
            }
        }
    }

    /**
     * Approves a friend request and updates the UI state.
     */
    fun approveRequest(friendId: Int) {
        viewModelScope.launch {
            val userId = getUserId() ?: return@launch
            try {
                val acceptedFriend = friendRequestApi.accept(userId, friendId)
                if (acceptedFriend != null) {
                    _uiState.update { state ->
                        state.copy(
                            friendRequests = state.friendRequests.filterNot { it.id == friendId },
                            friends = state.friends + acceptedFriend
                        )
                    }
                }
            } catch (e: Exception) {
                // Optionally handle the error
            }
        }
    }

    /**
     * Declines a friend request and updates the UI state.
     */
    fun declineRequest(friendId: Int) {
        viewModelScope.launch {
            val userId = getUserId() ?: return@launch
            try {
                val success = friendRequestApi.decline(userId, friendId)
                if (success) {
                    _uiState.update { state ->
                        state.copy(
                            friendRequests = state.friendRequests.filterNot { it.id == friendId }
                        )
                    }
                }
            } catch (e: Exception) {
                // Optionally handle the error
            }
        }
    }
}
