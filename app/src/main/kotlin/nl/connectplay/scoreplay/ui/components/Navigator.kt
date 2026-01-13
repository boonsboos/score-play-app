package nl.connectplay.scoreplay.ui.components

import androidx.annotation.RequiresPermission
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
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.screens.FriendsScreen
import nl.connectplay.scoreplay.screens.GameDetailScreen
import nl.connectplay.scoreplay.screens.GamesScreen
import nl.connectplay.scoreplay.screens.home.HomeScreen
import nl.connectplay.scoreplay.screens.LeaderboardScreen
import nl.connectplay.scoreplay.screens.LoginScreen
import nl.connectplay.scoreplay.screens.NotificationsScreen
import nl.connectplay.scoreplay.screens.profile.ProfileEditScreen
import nl.connectplay.scoreplay.screens.profile.ProfileScreen
import nl.connectplay.scoreplay.screens.RegisterScreen
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.screens.SearchScreen
import nl.connectplay.scoreplay.screens.profile.FollowedGamesScreen
import nl.connectplay.scoreplay.screens.profile.UserSessionsScreen
import nl.connectplay.scoreplay.screens.session.RoundDetailScreen
import nl.connectplay.scoreplay.screens.session.SessionFinishScreen
import nl.connectplay.scoreplay.screens.session.SessionScoreScreen
import nl.connectplay.scoreplay.screens.session.SessionSetupScreen
import nl.connectplay.scoreplay.screens.session.SessionDetailScreen
import nl.connectplay.scoreplay.viewModels.NotificationBadgeViewModel
import nl.connectplay.scoreplay.viewModels.main.MainViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun Navigator(modifier: Modifier = Modifier) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val notificationBadgeViewModel = koinInject<NotificationBadgeViewModel>()
    val tokenState by mainViewModel.tokenState.collectAsState()

    if (!tokenState.isLoaded) return

    LaunchedEffect(tokenState.token) {
        tokenState.token?.let { token ->
            notificationBadgeViewModel.startSse(token)
        }
    }

    val start = if (tokenState.token != null) Screens.Login else Screens.Login

    val backStack = rememberNavBackStack(start)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        // Add decorators to handle saved state, ViewModelStore, and scene setup
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        // Provide the composable content for each screen based on the navigation key
        entryProvider = { key ->
            when (key) {
                is Screens.Home -> NavEntry(key = key) {
                    HomeScreen(backStack)
                }

                is Screens.Profile -> NavEntry(key = key) {
                    ProfileScreen(
                        backStack,
                        targetUserId = key.userId
                    )
                }

                is Screens.EditProfile -> NavEntry(key = key) {
                    ProfileEditScreen(backStack, currentUser = key.currentUser)
                }

                is Screens.FollowedGames -> NavEntry(key = key) {
                    FollowedGamesScreen(backStack, targetUserId = key.userId)
                }

                is Screens.UserSessions -> NavEntry(key = key) {
                    UserSessionsScreen(backStack, targetUserId = key.userId)
                }

                is Screens.Friends -> NavEntry(key = key) {
                    FriendsScreen(backStack)
                }

                is Screens.Games -> NavEntry(key = key) {
                    GamesScreen(backStack)
                }

                is Screens.SessionSetup -> NavEntry(key = key) {
                    SessionSetupScreen(backStack)
                }

                is Screens.SessionScore -> NavEntry(key = key) {
                    SessionScoreScreen(backStack)
                }

                is Screens.RoundDetail -> NavEntry(key = key) {
                    RoundDetailScreen(
                        backStack,
                        sessionId = key.sessionId,
                        turn = key.turn,
                    )

                }

                is Screens.SessionFinish -> NavEntry(key = key) {
                    SessionFinishScreen(
                        backStack = backStack
                    )
                }

                is Screens.SessionDetail -> NavEntry(key = key) {
                    SessionDetailScreen(
                        backStack = backStack,
                        sessionId = key.sessionId,
                        userId = key.userId,
                        ownerName = key.ownerName
                    )
                }

                is Screens.GameDetail -> NavEntry(key = key) {
                    GameDetailScreen(
                        gameId = key.gameId,
                        backStack
                    )
                }

                is Screens.Notifications -> NavEntry(key = key) {
                    NotificationsScreen(
                        backStack
                    )
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
                        backStack,
                        // pass query string from nav key to screen
                        initialQuery = key.query,
                        searchViewModel = koinViewModel(),
                        onGameClick = { gameId ->
                            backStack.add(Screens.GameDetail(gameId = gameId.toInt()))
                        },
                        onUserClick = { userId ->
                            backStack.add(Screens.Profile(userId = userId))
                        }
                    )
                }

                is Screens.Leaderboard -> NavEntry(key = key) {
                    LeaderboardScreen(
                        gameId = key.gameId,
                        backStack
                    )
                }

                // Handle unknown destinations
                else -> error("Unknown destination: $key")
            }
        })
}