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
import org.junit.Test
import kotlinx.datetime.LocalDateTime

// -----------------
// SessionDetailViewModelTest
// ------------------------
class SessionDetailViewModelTest {

    // ------------------------
    // Dummy objects for testing
    // ------------------------
    private fun createDummyGame() = Game(
        id = 1,
        name = "TestGame",
        description = "A test game",
        publisher = "TestPublisher"
    )

    private fun createDummySession(game: Game) = Session(
        sessionId = "session1",
        game = game,
        hostId = 10,
        startTime = LocalDateTime(2026, 1, 1, 12, 0),
        endTime = LocalDateTime(2026, 1, 1, 13, 0),
        endOfSessionPictureUrl = null,
        visibility = SessionVisibility.PUBLIC
    )

    // ------------------------
    // Test: handleFetch success
    // ------------------------
    @Test
    fun `handleFetch updates state with session on success`() = runBlocking {
        // Arrange: create dummy session and mock API
        val game = createDummyGame()
        val session = createDummySession(game)
        val sessionApi = mockk<SessionApi>()

        coEvery { sessionApi.single(10, "session1") } returns session

        val viewModel = SessionDetailViewModel(sessionApi)

        // Act: fetch session
        viewModel.handleFetch(userId = 10, sessionId = "session1")

        // Wait until loading finishes
        val state: SessionDetailState = viewModel.state.first { !it.isLoading }

        // Assert: session is set and no error
        assertEquals(session, state.session)
        assertNull(state.error)
    }
}
