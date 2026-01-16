package nl.connectplay.scoreplay

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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

    // ------------------------
    // Mocked dependencies
    // ------------------------
    private lateinit var profileApi: ProfileApi
    private lateinit var friendsApi: FriendsApi
    private lateinit var tokenDataStore: TokenDataStore

    // ------------------------
    // Dummy test data
    // ------------------------
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

    private val emptyFriendRequests = FriendRequestListResponse(outstanding = emptyList())

    // ------------------------
    // Setup before each test
    // ------------------------
    @Before
    fun setup() {
        profileApi = mockk()
        friendsApi = mockk()
        tokenDataStore = mockk()

        // Default mock behavior
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
        // ------------------------
        // Arrange
        // ------------------------
        coEvery { profileApi.getLastSessions(1) } returns dummySessions

        // Initialize ViewModel with mocked dependencies
        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // ------------------------
        // Act
        // ------------------------

        // Wait for the first emission that is not Loading or Idle
        val state = withTimeout(3000) {
            viewModel.sessionsState
                .filterNotNull()
                .filter { it is UiState.Success }
                .firstOrNull()
        }
        // ------------------------
        // Assert
        // ------------------------

        // Assert that the state is Success and contains the correct sessions
        assertTrue(state is UiState.Success)
        val sessions = (state as UiState.Success).data
        assertEquals(2, sessions.size)

        // Assert session IDs
        val ids = sessions.map { it.id }
        assertTrue(ids.contains("1"))
        assertTrue(ids.contains("2"))
    }

    @Test
    fun `profileState returns success with profile`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------
        // Initialize ViewModel
        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // ------------------------
        // Act
        // ------------------------

        // Wait for first emission that is not Loading or Idle
        val state = withTimeout(1000) {
            viewModel.profileState
                .filterNotNull()       // ignore null emissions
                .take(1)               // take first relevant emission
                .first { it !is UiState.Loading && it !is UiState.Idle }
        }

        // ------------------------
        // Assert
        // ------------------------

        // Assert that the state is Success and contains the correct profile
        assertTrue(state is UiState.Success)
        val profile = (state as UiState.Success).data
        assertEquals(1, profile.id)
        assertEquals("TestUser", profile.username)
    }

    @Test
    fun `gamesState returns success with followed games`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------
        coEvery { profileApi.getFollowedGames(1) } returns dummyFollowedGames

        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // ------------------------
        // Act
        // ------------------------

        // Wait for first emission that is not Loading or Idle
        val state = withTimeout(1000) {
            viewModel.gamesState
                .filterNotNull()
                .take(1)
                .first { it !is UiState.Loading && it !is UiState.Idle }
        }

        // ------------------------
        // Assert
        // ------------------------

        // Assert that followed games are returned correctly
        assertTrue(state is UiState.Success)
        val games = (state as UiState.Success).data
        assertEquals(1, games.size)
        assertEquals("DummyGame", games.first().name)
    }

    @Test
    fun `friendshipStatus returns FRIENDS when user is already friend`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { profileApi.getProfile(2) } returns UserProfile(
            id = 2,
            username = "Bob",
            email = "bob@test.com"
        )
        coEvery { friendsApi.getFriends(1) } returns listOf(
            UserFriend(BareUser(2, "Bob"), FriendshipStatus.FRIENDS)
        )

        val viewModel = ProfileViewModel(
            userId = 2,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // ------------------------
        // Act
        // ------------------------

        // Wait for friendship status emission
        val status = withTimeout(1000) {
            viewModel.friendshipStatus
                .filterNotNull()
                .take(1)
                .first()
        }

        // ------------------------
        // Assert
        // ------------------------

        // Assert friendship status is FRIENDS
        assertEquals(FriendshipStatus.FRIENDS, status)
    }

    @Test
    fun `friendshipStatus returns PENDING when there is a pending request`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------
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

        // ------------------------
        // Act
        // ------------------------

        // Wait for friendship status emission
        val status = withTimeout(1000) {
            viewModel.friendshipStatus
                .filterNotNull()
                .take(1)
                .first()
        }

        // ------------------------
        // Assert
        // ------------------------

        // Assert friendship status is PENDING
        assertEquals(FriendshipStatus.PENDING, status)
    }
}