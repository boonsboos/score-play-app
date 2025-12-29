package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRight
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.game.FollowedGame
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.HomeViewModel
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    sessionViewModel: SessionViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val followedGames by homeViewModel.followedState.collectAsStateWithLifecycle()

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Home", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {

            // Start new session button
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        sessionViewModel.onEvent(SessionEvent.StartNewSession)
                        backStack.add(Screens.SessionSetup)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Start new session"
                    )
                    Text("Start new session")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recent played games carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // TODO: add carousel items
                }
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
                            text = "Error loading followed games: $message",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Success -> {
                    val games = (followedGames as UiState.Success<List<FollowedGame>>).data

                    if (games.isEmpty()) {
                        item {
                            Text(
                                text = "You are not following any games yet",
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
                                text = "Followed Games",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        // List items
                        items(
                            games,
                            key = { it.id }
                        ) { game ->
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
fun FollowedGameItem(
    game: FollowedGame,
    modifier: Modifier = Modifier,
    onUserClick: (playerId: Int) -> Unit = { }
) {
    var expanded by rememberSaveable(game.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = game.name,
            style = MaterialTheme.typography.titleMedium
        )

        if (expanded) {
            Spacer(Modifier.height(8.dp))

            game.podium.forEach { podiumItem ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    FallbackImage(url = null, size = 24.dp) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = podiumItem.playerName.first().toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = podiumItem.playerName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Score: " + podiumItem.score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = "View details",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
//                            .clickable { onUserClick(podiumItem.playerId) }
                    )
                }
            }
        }
    }
}