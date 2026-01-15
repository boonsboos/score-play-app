package nl.connectplay.scoreplay

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import nl.connectplay.scoreplay.api.FriendsApi
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.models.friends.FriendRequestListResponse
import nl.connectplay.scoreplay.models.friends.FriendshipStatus
import nl.connectplay.scoreplay.models.friends.UserFriend
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.user.BareUser
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.models.user.UserSession
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.profile.ProfileViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest {

    // Mocked dependencies for the ViewModel
    private lateinit var profileApi: ProfileApi
    private lateinit var friendsApi: FriendsApi
    private lateinit var tokenDataStore: TokenDataStore

    // Dummy test data for games, profiles, sessions, and friends
    private val dummyGame = Game(
        id = 1,
        name = "DummyGame",
        description = "A test game",
        publisher = "TestPublisher"
    )

    private val dummyUserProfile = UserProfile(
        id = 1,
        username = "TestUser",
        email = "test@example.com"
    )

    private val dummySessions = listOf(
        UserSession(
            id = "1",
            game = dummyGame,
            hostId = 10,
            startTime = LocalDateTime(2026, 1, 1, 12, 0),
            endTime = LocalDateTime(2026, 1, 1, 13, 0),
            endOfSessionPictureUrl = null,
            visibility = "public"
        ),
        UserSession(
            id = "2",
            game = dummyGame,
            hostId = 11,
            startTime = LocalDateTime(2026, 1, 2, 12, 0),
            endTime = LocalDateTime(2026, 1, 2, 13, 0),
            endOfSessionPictureUrl = null,
            visibility = "private"
        )
    )

    private val dummyFollowedGames = listOf(
        FollowedGame(
            id = 1,
            name = dummyGame.name,
            description = dummyGame.description,
            publisher = dummyGame.publisher
        )
    )

    private val emptyFriendRequests =
        FriendRequestListResponse(outstanding = emptyList())

    @Before
    fun setup() {
        // Initialize mocked dependencies before each test
        profileApi = mockk()
        friendsApi = mockk()
        tokenDataStore = mockk()

        // Default behaviors for mocks
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { friendsApi.getFriends(any()) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns emptyFriendRequests
        coEvery { profileApi.getProfile(any()) } returns dummyUserProfile
        coEvery { profileApi.getLastSessions(any()) } returns emptyList()
        coEvery { profileApi.getFollowedGames(any()) } returns emptyList()
    }

    // ------------------------
    // Tests
    // ------------------------

    @Test
    fun `sessionsState returns success with sessions`() = runBlocking {
        coEvery { profileApi.getLastSessions(1) } returns dummySessions

        // Create the ViewModel with mocked dependencies
        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Wait for the first non-loading, non-idle state
        val state = viewModel.sessionsState.first {
            it !is UiState.Loading && it !is UiState.Idle
        }

        // Assert that the state is successful and contains the correct sessions
        assertTrue(state is UiState.Success)
        val sessions = (state as UiState.Success).data
        assertEquals(2, sessions.size) // Should have 2 sessions
        assertEquals("1", sessions.first().id) // Verify first session ID
    }

    @Test
    fun `profileState returns success with profile`() = runBlocking {
        // Initialize the ViewModel
        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Wait for the first non-loading, non-idle state
        val state = viewModel.profileState.first {
            it !is UiState.Loading && it !is UiState.Idle
        }

        // Assert that the state is successful and contains the correct profile
        assertTrue(state is UiState.Success)
        val profile = (state as UiState.Success).data
        assertEquals(1, profile.id)
        assertEquals("TestUser", profile.username)
    }

    @Test
    fun `gamesState returns success with followed games`() = runBlocking {
        coEvery { profileApi.getFollowedGames(1) } returns dummyFollowedGames

        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        val state = viewModel.gamesState.first {
            it !is UiState.Loading && it !is UiState.Idle
        }

        // Assert that followed games are returned correctly
        assertTrue(state is UiState.Success)
        val games = (state as UiState.Success).data
        assertEquals(1, games.size)
        assertEquals("DummyGame", games.first().name)
    }

    @Test
    fun `friendshipStatus returns FRIENDS when user is already friend`() = runBlocking {
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { profileApi.getProfile(2) } returns UserProfile(
            id = 2,
            username = "Bob",
            email = "bob@test.com"
        )

        // The logged-in user's friends list includes the profile user
        coEvery { friendsApi.getFriends(1) } returns listOf(
            UserFriend(BareUser(2, "Bob"), FriendshipStatus.FRIENDS)
        )

        // Initialize the ViewModel
        val viewModel = ProfileViewModel(
            userId = 2,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Wait for friendshipStatus to be set
        val status = viewModel.friendshipStatus.first { it != null }

        // Assert that the friendship status is FRIENDS
        assertEquals(FriendshipStatus.FRIENDS, status)
    }

    @Test
    fun `friendshipStatus returns PENDING when there is a pending request`() = runBlocking {
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { profileApi.getProfile(3) } returns UserProfile(3, "Alice", "alice@test.com")
        coEvery { friendsApi.getFriends(1) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns FriendRequestListResponse(
            outstanding = listOf(
                UserFriend(
                    user = BareUser(id = 3, username = "Alice"),
                    status = FriendshipStatus.PENDING
                )
            )
        )

        val viewModel = ProfileViewModel(
            userId = 3,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        val status = viewModel.friendshipStatus.first { it != null }

        // Assert that the friendship status is PENDING
        assertEquals(FriendshipStatus.PENDING, status)
    }
}
