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
import org.junit.Test

class ProfileViewModelTest {

    // ------------------------
    // Dummy objects for test data
    // ------------------------

    /** Create a dummy Game object */
    private fun createDummyGame() = Game(
        id = 1,
        name = "DummyGame",
        description = "A test game",
        publisher = "TestPublisher"
    )

    /** Create a dummy user profile */
    private fun createDummyUserProfile() = UserProfile(
        id = 1,
        username = "TestUser",
        email = "test@example.com"
    )

    /** Create dummy sessions for a given game */
    private fun createDummySessions(game: Game) = listOf(
        UserSession(
            id = "1",
            game = game,
            hostId = 10,
            startTime = LocalDateTime(2026, 1, 1, 12, 0),
            endTime = LocalDateTime(2026, 1, 1, 13, 0),
            endOfSessionPictureUrl = null,
            visibility = "public"
        ),
        UserSession(
            id = "2",
            game = game,
            hostId = 11,
            startTime = LocalDateTime(2026, 1, 2, 12, 0),
            endTime = LocalDateTime(2026, 1, 2, 13, 0),
            endOfSessionPictureUrl = null,
            visibility = "private"
        )
    )

    /** Create dummy followed games based on a game */
    private fun createDummyFollowedGames(game: Game) = listOf(
        FollowedGame(
            id = 1,
            name = game.name,
            description = game.description,
            publisher = game.publisher
        )
    )

    /** Create dummy friends with FRIENDS status */
    private fun createDummyFriends() = listOf(
        UserFriend(
            user = BareUser(id = 2, username = "Bob"),
            status = FriendshipStatus.FRIENDS
        )
    )

    /** Create dummy pending friends */
    private fun createPendingFriends() = listOf(
        UserFriend(
            user = BareUser(id = 3, username = "Alice"),
            status = FriendshipStatus.PENDING
        )
    )

    /** Empty friend requests response */
    private fun emptyFriendRequests() = FriendRequestListResponse(outstanding = emptyList())

    /** Pending friend requests response */
    private fun pendingFriendRequests() = FriendRequestListResponse(outstanding = createPendingFriends())

    // ------------------------
    // Tests
    // ------------------------

    /** Test that sessionsState returns a success with dummy sessions */
    @Test
    fun `sessionsState returns success with sessions`() = runBlocking {
        val dummyGame = createDummyGame()
        val dummySessions = createDummySessions(dummyGame)

        // Mock dependencies
        val profileApi = mockk<ProfileApi>()
        val friendsApi = mockk<FriendsApi>()
        val tokenDataStore = mockk<TokenDataStore>()

        // Mocks for ProfileViewModel init { loadProfile() }
        coEvery { profileApi.getProfile(1) } returns createDummyUserProfile()
        coEvery { profileApi.getLastSessions(1) } returns dummySessions
        coEvery { profileApi.getFollowedGames(1) } returns emptyList()
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { friendsApi.getFriends(1) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns emptyFriendRequests()

        // Create ViewModel with mocked dependencies
        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Suspend until sessionsState is no longer Loading or Idle
        val state = viewModel.sessionsState.first { it !is UiState.Loading && it !is UiState.Idle }

        // Assert: the state is Success and contains correct sessions
        assertTrue(state is UiState.Success)
        val sessions = (state as UiState.Success).data
        assertEquals(2, sessions.size)
        assertEquals("1", sessions.first().id)
    }

    /** Test that profileState returns a success with the user profile */
    @Test
    fun `profileState returns success with profile`() = runBlocking {
        val profileApi = mockk<ProfileApi>()
        val friendsApi = mockk<FriendsApi>()
        val tokenDataStore = mockk<TokenDataStore>()

        // Mock API calls
        coEvery { profileApi.getProfile(1) } returns createDummyUserProfile()
        coEvery { profileApi.getLastSessions(1) } returns emptyList()
        coEvery { profileApi.getFollowedGames(1) } returns emptyList()
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { friendsApi.getFriends(1) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns emptyFriendRequests()

        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Suspend until profileState is no longer Loading or Idle
        val state = viewModel.profileState.first { it !is UiState.Loading && it !is UiState.Idle }

        // Assert: state is Success and contains correct profile data
        assertTrue(state is UiState.Success)
        val profile = (state as UiState.Success).data
        assertEquals(1, profile.id)
        assertEquals("TestUser", profile.username)
    }

    /** Test that gamesState returns a success with followed games */
    @Test
    fun `gamesState returns success with followed games`() = runBlocking {
        val dummyGame = createDummyGame()
        val dummyFollowedGames = createDummyFollowedGames(dummyGame)

        val profileApi = mockk<ProfileApi>()
        val friendsApi = mockk<FriendsApi>()
        val tokenDataStore = mockk<TokenDataStore>()

        // Mock API calls
        coEvery { profileApi.getFollowedGames(1) } returns dummyFollowedGames
        coEvery { profileApi.getProfile(1) } returns createDummyUserProfile()
        coEvery { profileApi.getLastSessions(1) } returns emptyList()
        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { friendsApi.getFriends(1) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns emptyFriendRequests()

        val viewModel = ProfileViewModel(
            userId = 1,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Suspend until gamesState is no longer Loading or Idle
        val state = viewModel.gamesState.first { it !is UiState.Loading && it !is UiState.Idle }

        // Assert: state is Success and contains correct followed games
        assertTrue(state is UiState.Success)
        val games = (state as UiState.Success).data
        assertEquals(1, games.size)
        assertEquals("DummyGame", games.first().name)
    }

    /** Test that friendshipStatus returns FRIENDS when the user is already a friend */
    @Test
    fun `friendshipStatus returns FRIENDS when user is already friend`() = runBlocking {
        val profileApi = mockk<ProfileApi>()
        val friendsApi = mockk<FriendsApi>()
        val tokenDataStore = mockk<TokenDataStore>()

        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { profileApi.getProfile(2) } returns UserProfile(id = 2, username = "TestUser", email = "test@example.com")
        coEvery { profileApi.getLastSessions(2) } returns emptyList()
        coEvery { profileApi.getFollowedGames(2) } returns emptyList()
        coEvery { friendsApi.getFriends(1) } returns listOf(UserFriend(BareUser(2, "Bob"), FriendshipStatus.FRIENDS))
        coEvery { friendsApi.getAllFriendRequests() } returns emptyFriendRequests()

        val viewModel = ProfileViewModel(
            userId = 2,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Suspend until friendshipStatus is non-null
        val status = viewModel.friendshipStatus.first { it != null }

        // Assert: friendship status is FRIENDS
        assertEquals(FriendshipStatus.FRIENDS, status)
    }

    /** Test that friendshipStatus returns PENDING when a request exists */
    @Test
    fun `friendshipStatus returns PENDING when there is a pending request`() = runBlocking {
        val profileApi = mockk<ProfileApi>()
        val friendsApi = mockk<FriendsApi>()
        val tokenDataStore = mockk<TokenDataStore>()

        coEvery { tokenDataStore.userId } returns flowOf(1)
        coEvery { profileApi.getProfile(3) } returns UserProfile(id = 3, username = "TestUser", email = "test@example.com")
        coEvery { profileApi.getLastSessions(3) } returns emptyList()
        coEvery { profileApi.getFollowedGames(3) } returns emptyList()
        coEvery { friendsApi.getFriends(1) } returns emptyList()
        coEvery { friendsApi.getAllFriendRequests() } returns pendingFriendRequests()

        val viewModel = ProfileViewModel(
            userId = 3,
            profileApi = profileApi,
            friendsApi = friendsApi,
            tokenDataStore = tokenDataStore
        )

        // Suspend until friendshipStatus is non-null
        val status = viewModel.friendshipStatus.first { it != null }

        // Assert: friendship status is PENDING
        assertEquals(FriendshipStatus.PENDING, status)
    }
}
