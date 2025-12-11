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

data class FriendsUiState(
    val friendRequests: List<FriendRequest> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false
)

class FriendViewModel(
    private val friendsApi: FriendsApi,
    private val friendRequestApi: FriendRequestApi,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private val hardcodedUserId = 10

    init { refreshData() }

    fun refreshData() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                println("Fetching friends for userId=$hardcodedUserId")
                val friends = friendsApi.getFriendsById(hardcodedUserId)
                println("Friends fetched: count=${friends.size}")
                friends.forEach { println("   • Friend: ${it.username}, status=${it.status}") }
                val requests = friendRequestApi.getAllFriendrequests(hardcodedUserId)

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
            val acceptedFriend = friendRequestApi.accept(hardcodedUserId, friendId)
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
            val success = friendRequestApi.decline(hardcodedUserId, friendId)
            if (success) {
                _uiState.update { state ->
                    state.copy(friendRequests = state.friendRequests.filterNot { it.id == friendId })
                }
            }
        }
    }
}
