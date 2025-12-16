package nl.connectplay.scoreplay.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.screens.ExampleDetailScreen
import nl.connectplay.scoreplay.screens.ExampleScreen
import nl.connectplay.scoreplay.screens.FriendsScreen
import nl.connectplay.scoreplay.screens.GamesScreen
import nl.connectplay.scoreplay.screens.HomeScreen
import nl.connectplay.scoreplay.screens.NewSessionScreen
import nl.connectplay.scoreplay.screens.LoginScreen
import nl.connectplay.scoreplay.screens.NotificationsScreen
import nl.connectplay.scoreplay.screens.ProfileEditScreen
import nl.connectplay.scoreplay.screens.ProfileScreen
import nl.connectplay.scoreplay.screens.RegisterScreen
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import nl.connectplay.scoreplay.screens.SearchScreen
import nl.connectplay.scoreplay.viewModels.main.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Navigator(modifier: Modifier = Modifier) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val tokenState by mainViewModel.tokenState.collectAsState()

    if (!tokenState.isLoaded) return

    val start = if (tokenState.token != null) Screens.Home else Screens.Login

    val backStack = rememberNavBackStack(
        start
//            Screens.EditProfile(UserProfile(id = 1, username = "Bob", email = "bob@email.com"))
    )

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
                        backStack,
                        onLogout = {
                            mainViewModel.logout()
                            backStack.apply {
                                while (isNotEmpty()) removeLast()
                                add(Screens.Login)
                            }
                        }
                    )
                }

                is Screens.Profile -> NavEntry(key = key) {
                    ProfileScreen(backStack = backStack, targetUserId = key.userId)
                }

                is Screens.EditProfile -> NavEntry(key = key) {
                    ProfileEditScreen(backStack = backStack, currentUser = key.currentUser)
                }

                is Screens.Friends -> NavEntry(key = key) {
                    FriendsScreen(backStack)
                }

                is Screens.Games -> NavEntry(key = key) {
                    GamesScreen(backStack)
                }

                is Screens.NewSession -> NavEntry(key = key) {
                    val sessionViewModel: SessionViewModel = koinViewModel()

                    NewSessionScreen(
                        backStack = backStack,
                        onEvent = sessionViewModel::onEvent
                    )
                }

                is Screens.Notifications -> NavEntry(key = key) {
                    NotificationsScreen(backStack = backStack)
                }

                is Screens.Login -> NavEntry(key = key) {
                    LoginScreen(
                        viewModel = koinViewModel(),
                        onNavigateToRegister = { backStack.add(Screens.Register) },
                        onLoginSuccess = { backStack.add(Screens.Home) }
                    )
                }

                is Screens.Register -> NavEntry(key = key) {
                    RegisterScreen(
                        onNavigateToLogin = { backStack.add(Screens.Login) }
                    )
                }

                is Screens.Search -> NavEntry(key = key) {
                    SearchScreen(
                        backStack = backStack,
                        // pass query string from nav key to screen
                        initialQuery = key.query,
                        searchViewModel = koinViewModel()
                    )
                }

                // Handle unknown destinations
                else -> error("Unknown destination: $key")
            }
        })
}