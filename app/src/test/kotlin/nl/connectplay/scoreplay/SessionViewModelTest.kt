package nl.connectplay.scoreplay

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.LocalDateTime
import nl.connectplay.scoreplay.api.SessionApi
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.models.session.Session
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.viewModels.session.SessionDetailState
import nl.connectplay.scoreplay.viewModels.session.SessionDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SessionDetailViewModelTest {

    // ------------------------
    // Dummy test data
    // ------------------------
    private val dummyGame = Game(
        id = 1,
        name = "TestGame",
        description = "A test game",
        publisher = "TestPublisher"
    )

    private val dummySession = Session(
        sessionId = "session1",
        game = dummyGame,
        hostId = 10,
        startTime = LocalDateTime(2026, 1, 1, 12, 0),
        endTime = LocalDateTime(2026, 1, 1, 13, 0),
        endOfSessionPictureUrl = null,
        visibility = SessionVisibility.PUBLIC
    )

    // ------------------------
    // Mocks & ViewModel
    // ------------------------
    private lateinit var sessionApi: SessionApi
    private lateinit var viewModel: SessionDetailViewModel

    @Before
    fun setUp() {
        // Mock SessionApi
        sessionApi = mockk()
        // Stub the API call to return the dummy session
        coEvery { sessionApi.single(10, "session1") } returns dummySession
        coEvery { sessionApi.allScores("session1") } returns emptyList()

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

        // Wait for the first state where loading has finished
        // We use withTimeout to prevent infinite waiting in case of errors
        val state: SessionDetailState = withTimeout(1000) {
            viewModel.state
                .filterNotNull()
                .take(1)
                .first { !it.isLoading }
        }

        // ------------------------
        // Assert
        // ------------------------

        // Assert: check that session is loaded correctly
        assertEquals(dummySession, state.session)
        assertNull(state.error)
    }
}