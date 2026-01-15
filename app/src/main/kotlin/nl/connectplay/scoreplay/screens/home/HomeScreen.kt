package nl.connectplay.scoreplay.screens.home

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.room.util.TableInfo
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FollowedGameItem
import nl.connectplay.scoreplay.ui.components.PullToRefresh
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ScorePlayTopBar(
                title = stringResource(R.string.screen_home_title), backStack = backStack
            )
        },
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        PullToRefresh(
            onRefresh = { homeViewModel.refresh() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Start new session button
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp), onClick = {
                            backStack.add(Screens.SessionSetup(startNew = true))
                        }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.new_session_button)
                        )
                        Text(text = stringResource(R.string.new_session_button))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Recent played games carousel
                item {
                    when (recentGames) {
                        UiState.Loading -> {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.screen_recently_played_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }

                        is UiState.Error -> {
                            val message = (recentGames as UiState.Error).message

                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.screen_recently_played_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = stringResource(R.string.recent_games_error, message),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        is UiState.Success -> {
                            val games = (recentGames as UiState.Success).data
                            if (games.isEmpty()) {
                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_recently_played_title),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = stringResource(R.string.recently_played_empty),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_recently_played_title),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    LazyRow(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(games) { game ->
                                            RecentGamesItem(
                                                game = game, onGameClick = {
                                                    backStack.add(Screens.GameDetail(game.id))
                                                })
                                        }
                                    }
                                }
                            }
                        }

                        else -> Unit
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when (followedGames) {
                    UiState.Loading -> {
                        item {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.screen_followed_games_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }

                    is UiState.Error -> {
                        val message = (followedGames as UiState.Error).message
                        item {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.screen_followed_games_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = stringResource(R.string.followed_games_error, message),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    is UiState.Success -> {
                        val games = (followedGames as UiState.Success<List<FollowedGame>>).data

                        if (games.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 24.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_followed_games_title),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = stringResource(R.string.followed_games_empty),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp)
                                    )
                                }
                            }
                        } else {
                            // Header
                            item {
                                Column(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp,
                                        horizontal = 24.dp
                                    ),
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_followed_games_title),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                            }

                            // List items
                            items(games, key = { it.id }) { game ->
                                FollowedGameItem(game = game)
                                if (games.size > 1 && game != games.last()) HorizontalDivider()
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun RecentGamesItem(game: Game, modifier: Modifier = Modifier, onGameClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 16.dp)
            .clickable { onGameClick() }) {
        Text(
            text = game.name, style = MaterialTheme.typography.titleMedium
        )
    }
}