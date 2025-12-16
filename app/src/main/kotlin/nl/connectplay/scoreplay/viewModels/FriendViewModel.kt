package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.call.NoTransformationFoundException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.models.friends.FriendRequestListResponse
import nl.connectplay.scoreplay.models.friends.UserFriend
import nl.connectplay.scoreplay.stores.TokenDataStore

/**
 * UI state for the FriendList screen
 */
data class FriendsUiState(
    val friendRequests: FriendRequestListResponse =
        FriendRequestListResponse(pending = emptyList(), outstanding = emptyList()),
    val friends: List<UserFriend> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FriendViewModel(
    private val friendsApi: FriendsApi,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    /**
     * Backing property for UI state
     */
    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private suspend fun getUserId(): Int? {
        return tokenDataStore.userId.firstOrNull()
    }

    init {
        refreshData()
    }

    /**
     * Refreshes both friends and friend requests for the logged-in user.
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
                val friends: List<UserFriend> = friendsApi.getFriends(userId).sortedBy { it.user.username.lowercase() }
                val requests: FriendRequestListResponse = friendsApi.getAllFriendRequests().let { resp ->
                    resp.copy(
                        pending = resp.pending.sortedBy { it.user.username.lowercase() },
                        outstanding = resp.outstanding.sortedBy { it.user.username.lowercase() }
                    )
                }

                _uiState.update {
                    it.copy(
                        friends = friends,
                        friendRequests = requests,
                        isLoading = false
                    )
                }
            } catch (e: NoTransformationFoundException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    /**
     * Approve a pending friend request and move it to the friend list.
     */
    fun approveRequest(friendId: Int) {
        viewModelScope.launch {
            val success = friendsApi.accept(friendId) // Boolean

            if (success) {
                _uiState.update { state ->
                    state.copy(
                        friendRequests = state.friendRequests.copy(
                            pending = state.friendRequests.pending
                                .filterNot { it.user.id == friendId }
                        )
                    )
                }
                refreshData()
            }
        }
    }


    /**
     * Decline a pending request and remove it from the UI state.
     */
    fun declineRequest(friendId: Int) {
        viewModelScope.launch {
            val success = friendsApi.decline(friendId) // Boolean

            if (success) {
                _uiState.update { state ->
                    state.copy(
                        friendRequests = state.friendRequests.copy(
                            pending = state.friendRequests.pending
                                .filterNot { it.user.id == friendId }
                        )
                    )
                }
            }
            refreshData()
        }
    }

}
