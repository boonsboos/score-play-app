package nl.connectplay.scoreplay.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import nl.connectplay.scoreplay.screens.ExampleDetailScreen
import nl.connectplay.scoreplay.screens.ExampleScreen
import nl.connectplay.scoreplay.screens.FriendsScreen
import nl.connectplay.scoreplay.screens.GamesScreen
import nl.connectplay.scoreplay.screens.HomeScreen
import nl.connectplay.scoreplay.screens.NotificationsScreen
import nl.connectplay.scoreplay.screens.Screens
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Navigator(modifier: Modifier = Modifier) {
    // Create a navigation back stack starting at the Home screen
    val backStack = rememberNavBackStack(Screens.Home)
    NavDisplay(
        modifier = modifier, backStack = backStack,
        // Add decorators to handle saved state, ViewModelStore, and scene setup
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        // Provide the composable content for each screen based on the navigation key
        entryProvider = { key ->
            when (key) {
                /*
                 * If the key is Screens.Example, show the ExampleScreen
                 * and navigate to ExampleDetail on item click.
                 * The screen needs to be wrapped in a NavEntry.
                 */
                is Screens.Example -> NavEntry(key = key) {
                    ExampleScreen(onClick = { exampleId ->
                        backStack.add(
                            Screens.ExampleDetail(exampleId)
                        )
                    })
                }

                is Screens.ExampleDetail -> NavEntry(key = key) {
                    ExampleDetailScreen(
                        viewModel = koinViewModel {
                            parametersOf(key.id)
                        },
                        onBackClick = {
//                            backStack.removeLast()
                        }
                    )
                }

                is Screens.Home -> NavEntry(key = key) {
                    HomeScreen(backStack)
                }

                is Screens.Friends -> NavEntry(key = key) {
                    FriendsScreen(backStack)
                }

                is Screens.Games -> NavEntry(key = key) {
                    GamesScreen(backStack)
                }

                is Screens.Notifications -> NavEntry(key = key) {
                    NotificationsScreen(backStack)
                }
                // Handle unknown destinations
                else -> error("Unknown destination: $key")
            }
        })
}