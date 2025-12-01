package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendRequest
import nl.connectplay.scoreplay.models.FriendRequestStatus

data class FriendsUiState(
    val friendRequests: List<FriendRequest> = emptyList(),
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false,
)

sealed class FriendsEvent {
    object Refresh : FriendsEvent()
}

class FriendViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<FriendsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _uiState.update {
            it.copy(
                friendRequests = listOf(
                    FriendRequest("1", "USERNAME_T", "A", FriendRequestStatus.INCOMING),
                    FriendRequest("2", "USERNAME_I", "B", FriendRequestStatus.INCOMING),
                    FriendRequest("3", "USERNAME_K", "C", FriendRequestStatus.PENDING)
                ),
                friends = listOf(
                    Friend("10", "USERNAME_A", "F"),
                    Friend("11", "USERNAME_B", "U"),
                    Friend("12", "USERNAME_C", "C"),
                    Friend("13", "USERNAME_D", "K"),
                )
            )
        }
    }

    fun approveRequest(id: String) {
        val req = _uiState.value.friendRequests.find { it.id == id } ?: return
        _uiState.update { state ->
            state.copy(
                friendRequests = state.friendRequests.filterNot { it.id == id },
                friends = state.friends + Friend(req.id, req.username, req.avatarLetter)
            )
        }
    }

    fun declineRequest(id: String) {
        _uiState.update { state ->
            state.copy(friendRequests = state.friendRequests.filterNot { it.id == id })
        }
    }
}
