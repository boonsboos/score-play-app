package nl.connectplay.scoreplay.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import nl.connectplay.scoreplay.screens.ExampleDetailScreen
import nl.connectplay.scoreplay.screens.ExampleScreen
import nl.connectplay.scoreplay.screens.FriendsScreen
import nl.connectplay.scoreplay.screens.GamesScreen
import nl.connectplay.scoreplay.screens.HomeScreen
import nl.connectplay.scoreplay.screens.LoginScreen
import nl.connectplay.scoreplay.screens.NotificationsScreen
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.viewModels.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Navigator(modifier: Modifier = Modifier) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val tokenState by mainViewModel.tokenState.collectAsState()

    if (!tokenState.isLoaded) {
        return
    }

    val start = if (tokenState.token != null) Screens.Home else Screens.Login

    // Create a navigation back stack starting at the Home screen
    val backStack = rememberNavBackStack(start)

    NavDisplay(
        modifier = modifier, backStack = backStack,
        // Add decorators to handle saved state, ViewModelStore, and scene setup
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
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
                    HomeScreen(
                        backStack = backStack,
                        onLogout = {
                            mainViewModel.logout()
                            backStack.apply {
                                while (isNotEmpty()) removeLast()
                                add(Screens.Login)
                            }
                        }
                    )
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

                is Screens.Login -> NavEntry(key = key) {
                    LoginScreen(
                        viewModel = koinViewModel(),
                        onNavigateToRegister = { backStack.add(Screens.Home) }
                    )
                }

                // Handle unknown destinations
                else -> error("Unknown destination: $key")
            }
        }
    )
}