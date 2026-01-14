package nl.connectplay.scoreplay

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import nl.connectplay.scoreplay.models.session.PlayerUi
import nl.connectplay.scoreplay.ui.components.session.PlayerRow
import org.junit.Rule
import org.junit.Test

class PlayerRowTest {

    /**
     * AndroidComposeRule spins up a real Activity for instrumented Compose UI tests.
     * This gives:
     * - an Android Context
     * - a Compose runtime to render composable
     * - Compose testing APIs
     */
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsNameAndAvatarLetter() {
        /**
         * GIVEN: a current user player.
         * For current user, the delete button should NOT be shown,
         * but the avatar initial and name should always be visible.
         */
        val player = PlayerUi(id = 1, name = "Alice", isCurrentUser = true)

        rule.setContent {
            /**
             * Wrap in MaterialTheme because PlayerRow uses MaterialTheme colorScheme/typography.
             * Without a theme, Material3 components can behave differently or fail in some setups.
             */
            MaterialTheme {
                PlayerRow(player = player, onRemove = {})
            }
        }

        /**
         * THEN: the player name is visible in the row.
         */
        rule.onNodeWithText("Alice").assertExists()

        /**
         * THEN: the avatar shows the first letter of the player's name ("A").
         */
        rule.onNodeWithText("A").assertExists()
    }

    @Test
    fun whenNotCurrentUser_deleteButtonIsVisible_andClickCallsOnRemove() {
        /**
         * GIVEN: a non-current-user player.
         * For non-current users, PlayerRow renders a delete IconButton with a contentDescription.
         */
        val player = PlayerUi(id = 2, name = "Bob", isCurrentUser = false)

        /**
         * We use a mutable flag to verify that the onRemove callback is invoked after clicking delete.
         */
        var removed = false

        /**
         * Fetch the exact localized contentDescription from Android resources.
         */
        val removeLabel = rule.activity.getString(R.string.player_row_remove)

        rule.setContent {
            MaterialTheme{
                PlayerRow(
                    player = player,
                    onRemove = { removed = true }
                )
            }
        }

        /**
         * THEN: delete button exists (identified by its contentDescription).
         */
        rule.onNodeWithContentDescription(removeLabel).assertExists()

        /**
         * WHEN: user taps the delete button.
         */
        rule.onNodeWithContentDescription(removeLabel).performClick()

        /**
         * THEN: onRemove callback should have been triggered.
         * runOnIdle ensures we assert after Compose has processed events and recompositions.
         */
        rule.runOnIdle {
            assert(removed)
        }
    }

    @Test
    fun whenCurrentUser_deleteButtonIsNotVisible() {
        /**
         * GIVEN: the current user.
         * PlayerRow should NOT show the delete button for the current user.
         */
        val player = PlayerUi(id = 3, name = "Tippe", isCurrentUser = true)

        /**
         * Same contentDescription as used when delete is visible.
         * We assert it does not exist in the semantics tree.
         */
        val removeLabel = rule.activity.getString(R.string.player_row_remove)

        rule.setContent {
            MaterialTheme {
                PlayerRow(player = player, onRemove = {})
            }
        }

        /**
         * THEN: delete button is absent.
         * assertDoesNotExist checks that no node with this contentDescription is present.
         */
        rule.onNodeWithContentDescription(removeLabel).assertDoesNotExist()
    }
}