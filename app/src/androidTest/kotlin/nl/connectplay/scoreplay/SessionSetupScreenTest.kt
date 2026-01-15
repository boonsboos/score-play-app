package nl.connectplay.scoreplay

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.viewModels.session.SessionState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.datetime.LocalDate
import org.junit.Rule
import org.junit.Test

class SessionSetupScreenTest {

    /**
     * AndroidComposeRule launches a ComponentActivity for the test, giving:
     * - a proper Android Context
     * - Compose test APIs
     */
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun `FAB hidden when no game selected`() {
        /**
         * We keep a reference to the Navigation3 back stack used by the composable.
         * This is optional for this test, but useful when you want to assert navigation later.
         */
        var bs: NavBackStack<NavKey>? = null

        /**
         * We keep state in a Compose mutableState so the UI can react if our onEvent changes it.
         * This mimics a ViewModel updating state.
         *
         * GIVEN: SessionState where no game is selected.
         */
        val initialState by mutableStateOf(SessionState(gameId = null))

        rule.setContent {
            /**
             * Create a real Navigation3 back stack at composition time.
             * We start on the SessionSetup screen, matching how the real app uses this screen.
             */
            val backStack = rememberNavBackStack(Screens.SessionSetup)
            /**
             * SideEffect runs after successful composition.
             * We use it to store the composed backStack reference in the test variable.
             */
            SideEffect { bs = backStack }

            // Render the screen in a realistic Material3 environment.
            MaterialTheme {
                /**
                 * We test the "pure" content composable (SessionSetupContent), not the DI wrapper.
                 * That avoids Koin, TokenDataStore, and ViewModel plumbing in UI tests.
                 */
                SessionSetupContent(
                    state = initialState,
                    games = emptyList(),
                    friends = emptyList(),
                    loading = false,
                    userId = 1,
                    onEvent = {},
                    backStack = backStack,
                )
            }
        }

        /**
         * THEN: The continue FAB must not exist.
         * Weu added `Modifier.testTag("continueFab")` to the FloatingActionButton.
         */
        rule.onNodeWithTag("continueFab").assertDoesNotExist()
    }

    @Test
    fun `FAB visible when game selected click saves and navigates`() {
        var bs: NavBackStack<NavKey>? = null


        // We track whether the UI emitted a SaveSession event.
        var saveCalled = false

        /**
         * GIVEN: A game is already selected (gameId != null), so the FAB should be visible.
         */
        var state by mutableStateOf(SessionState(gameId = 1))

        rule.setContent {
            val backStack = rememberNavBackStack(Screens.SessionSetup)
            SideEffect { bs = backStack }

            MaterialTheme {
                SessionSetupContent(
                    backStack = backStack,
                    state = state,
                    /**
                     * Provide at least one game matching state.gameId so selected-game UI can render.
                     */
                    games = listOf(Game(
                        id = 1, name = "Chess",
                        scoringMethodId = 1,
                        description = "This is a Chess game",
                        minPlayers = 2,
                        maxPlayers = 2,
                        duration = 90,
                        minAge = 3,
                        releaseDate = LocalDate(1300, 1, 1),
                        publisher = "Nobody",
                        pictures = emptyList()
                    )),
                    friends = emptyList(),
                    loading = false,
                    userId = 1,
                    /**
                     * Fake event handler:
                     * - If SaveSession is fired, mark saveCalled = true
                     * - If SetGame is fired, update state like a ViewModel would (so UI recomposes)
                     * - Ignore other events for this test
                     */
                    onEvent = { e ->
                        when (e) {
                            SessionEvent.SaveSession -> saveCalled = true
                            is SessionEvent.SetGame -> state = state.copy(gameId = e.gameId)
                            else -> Unit
                        }
                    }
                )
            }
        }

        /**
         * WHEN: we click the FAB.
         * The screen should:
         * 1) emit SaveSession
         * 2) navigate to Screens.SessionScore via backStack.add(Screens.SessionScore)
         */
        rule.onNodeWithTag("continueFab").assertExists().performClick()

        /**
         * THEN: verify side effects after the UI thread is idle (all recompositions done).
         * - saveCalled must be true
         * - the back stack should now end with Screens.SessionScore
         */
        rule.runOnIdle {
            assert(saveCalled)
            assert(bs?.lastOrNull() == Screens.SessionScore)
        }
    }

}