package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.FriendRequestApi
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendRequest
import nl.connectplay.scoreplay.stores.UserDataStore

/**
 * Represents the current state of the Friends screen.
 *
 * @property friendRequests A list of incoming friend requests
 * @property friends A list of the user's accepted friends
 * @property isLoading True if data is being fetched from the backend
 */
data class FriendsUiState(
    val friendRequests: List<FriendRequest> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false
)

/**
 * ViewModel that manages friends and friend requests.
 *
 * It interacts with the backend through FriendsApi and FriendRequestApi.
 *
 * @param friendsApi API used to fetch the user's current friends
 * @param friendRequestApi API used to fetch, approve, and decline friend requests
 * @param userId The ID of the currently logged-in user
 */
class FriendViewModel(
    private val friendsApi: FriendsApi,
    private val friendRequestApi: FriendRequestApi,
    private val userDataStore: UserDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val userId = userDataStore.userId.first() ?: return@launch
                val friends = friendsApi.getFriendsById(userId)
                val requests = friendRequestApi.getAllFriendrequests(userId)

                _uiState.update {
                    it.copy(
                        friends = friends,
                        friendRequests = requests,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun approveRequest(friendId: Int) {
        viewModelScope.launch {
            val userId = userDataStore.userId.first() ?: return@launch
            val acceptedFriend = friendRequestApi.accept(userId, friendId)
            if (acceptedFriend != null) {
                _uiState.update { state ->
                    state.copy(
                        friendRequests = state.friendRequests.filterNot { it.id == friendId },
                        friends = state.friends + acceptedFriend
                    )
                }
            }
        }
    }

    fun declineRequest(friendId: Int) {
        viewModelScope.launch {
            val userId = userDataStore.userId.first() ?: return@launch
            val success = friendRequestApi.decline(userId, friendId)
            if (success) {
                _uiState.update { state ->
                    state.copy(
                        friendRequests = state.friendRequests.filterNot { it.id == friendId }
                    )
                }
            }
        }
    }
}
