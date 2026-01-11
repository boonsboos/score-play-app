package nl.connectplay.scoreplay.screens.home

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FollowedGameItem
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.HomeViewModel
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>, homeViewModel: HomeViewModel = koinViewModel()
) {
    val followedGames by homeViewModel.followedState.collectAsStateWithLifecycle()
    val recentGames by homeViewModel.recentState.collectAsStateWithLifecycle()

    LaunchedEffect(followedGames) {
        if (followedGames is UiState.Error) {
            val exception = (followedGames as UiState.Error).exception
            if (exception is InvalidTokenException) {
                backStack.apply {
                    while (isNotEmpty()) removeAt(lastIndex)
                    add(Screens.Login)
                }
            }
        }
    }
    LaunchedEffect(recentGames) {
        if (recentGames is UiState.Error) {
            val exception = (recentGames as UiState.Error).exception
            if (exception is InvalidTokenException) {
                backStack.apply {
                    while (isNotEmpty()) removeAt(lastIndex)
                    add(Screens.Login)
                }
            }
        }
    }

    val activity = LocalContext.current as? ComponentActivity ?: return
    val sessionViewModel: SessionViewModel = koinViewModel(viewModelStoreOwner = activity)
    val onEvent = sessionViewModel::onEvent

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = stringResource(R.string.screen_home_title), backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            // Start new session button
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ), shape = RoundedCornerShape(12.dp), onClick = {
                        onEvent(SessionEvent.StartNewSession)
                        backStack.add(Screens.SessionSetup)
                    }) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.new_session_button)
                    )
                    Text(text = stringResource(R.string.new_session_button))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recent played games carousel
            item {
                when (recentGames) {
                    UiState.Loading -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    is UiState.Error -> {
                        val message = (recentGames as UiState.Error).message

                        Text(
                            text = stringResource(R.string.recent_games_error, message),
                            color = MaterialTheme.colorScheme.error
                        )

                    }

                    is UiState.Success -> {
                        val games = (recentGames as UiState.Success).data
                        if (games.isEmpty()) {
                            Text(
                                text = stringResource(R.string.recently_played_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.screen_recently_played_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                            LazyRow(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(games) { game ->
                                    RecentGamesItem(game = game)
                                }
                            }

                        }
                    }

                    else -> Unit
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (followedGames) {
                UiState.Loading -> {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                is UiState.Error -> {
                    val message = (followedGames as UiState.Error).message
                    item {
                        Text(
                            text = stringResource(R.string.followed_games_error, message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    val games = (followedGames as UiState.Success<List<FollowedGame>>).data

                    if (games.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.followed_games_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                            )
                        }
                    } else {
                        // Header
                        item {
                            Text(
                                text = stringResource(R.string.screen_followed_games_title), style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // List items
                        items(
                            games, key = { it.id }) { game ->
                            HorizontalDivider()
                            FollowedGameItem(game = game) {
                                backStack.add(Screens.Profile(it))
                            }
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
fun RecentGamesItem(game: Game, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Text(
            text = game.name,
            style = MaterialTheme.typography.titleMedium
        )
    }
}