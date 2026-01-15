package nl.connectplay.scoreplay

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.viewModels.session.SessionDetailViewModel
import nl.connectplay.scoreplay.viewModels.session.SessionDetailState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.datetime.LocalDateTime

class SessionDetailViewModelTest {

    // ------------------------
    // Class-level dummy objects
    // ------------------------
    private lateinit var dummyGame: Game
    private lateinit var dummySession: Session
    private lateinit var sessionApi: SessionApi
    private lateinit var viewModel: SessionDetailViewModel

    @Before
    fun setUp() {
        // Initialize dummy Game
        dummyGame = Game(
            id = 1,
            name = "TestGame",
            description = "A test game",
            publisher = "TestPublisher"
        )

        // Initialize dummy Session
        dummySession = Session(
            sessionId = "session1",
            game = dummyGame,
            hostId = 10,
            startTime = LocalDateTime(2026, 1, 1, 12, 0),
            endTime = LocalDateTime(2026, 1, 1, 13, 0),
            endOfSessionPictureUrl = null,
            visibility = SessionVisibility.PUBLIC
        )

        // Mock the SessionApi
        sessionApi = mockk()
        coEvery { sessionApi.single(10, "session1") } returns dummySession

        // Initialize ViewModel with mocked API
        viewModel = SessionDetailViewModel(sessionApi)
    }

    // ------------------------
    // Tests
    // ------------------------
    @Test
    fun `handleFetch updates state with session on success`() = runBlocking {
        // Act: fetch the session
        viewModel.handleFetch(userId = 10, sessionId = "session1")

        // Assert: state should be updated with the dummy session
        val state: SessionDetailState = viewModel.state.first { !it.isLoading }
        assertEquals(dummySession, state.session)
        assertNull(state.error)
    }
}