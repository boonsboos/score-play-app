package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ExpandableText
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GameDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    gameDetail: GameDetailViewModel = koinViewModel(parameters = { parametersOf(gameId) })
) {
    val state by gameDetail.gameState.collectAsState()
    val loading by gameDetail.loadingState.collectAsState()

    val carouselState = rememberCarouselState { state?.pictures?.size ?: 0 }
    val snackBarState = remember { SnackbarHostState()  }
    val coroutineScope = rememberCoroutineScope()

    if (loading) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = { ScorePlayTopBar(title = state?.name ?: "Name unknown", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = 300.dp,
                    itemSpacing = 10.dp
                ) { index ->
                    FallbackImage(
                        url = state?.pictures?.getOrNull(index),
                        size = 300.dp
                    ) {
                        Text("No images yet")
                    }
                }
            }

            // button group
            Row {
                // Start a new session
                FilledIconButton(onClick = {
                    // TODO: new session
                }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "New session"
                    )
                }

                // Redirect to show leaderboard
                FilledTonalButton(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        backStack.add(Screens.Leaderboard(gameId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.StackedBarChart,
                        contentDescription = "Leaderboard"
                    )
                    Text("Leaderboard")
                }

                // Bookmark toggle button
                OutlinedIconToggleButton(
                    checked = state?.following ?: false,
                    onCheckedChange = {
                        coroutineScope.launch {
                            val success = gameDetail.toggleFollow(gameId)
                            if (!success) {
                                val message = if (state?.following == true) "unfollow" else "follow"
                                snackBarState.showSnackbar("Failed to $message this game")
                            }
                        }
                    }
                ) {
                    if (state?.following == true) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Unfollow game"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Follow game"
                        )
                    }
                }

                // Game edit button
                OutlinedIconButton(onClick = {
                    // TODO: game edit screen
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 6.dp),
                thickness = 1.dp
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)) {
                Column {
                    Text(text = "Description", style = MaterialTheme.typography.headlineMedium)
                    ExpandableText(state?.description ?: "No description found")
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 6.dp),
                thickness = 1.dp
            )

            Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column {
                    Text(text = "Details", style = MaterialTheme.typography.headlineMedium)
                    GameDetailIfPresent("Publisher", state?.publisher)
                    GameDetailIfPresent("Duration", state?.duration?.let { "$it minutes" })
                    GameDetailIfPresent("Players", state?.minPlayers?.let { min ->
                        state?.maxPlayers?.let { max ->
                            if (min == max) "$min"
                            else "$min - $max"
                        }
                    })
                    GameDetailIfPresent("Release date", state?.releaseDate?.toString())
                }
            }

        }
    }
}

@Composable
private fun GameDetailIfPresent(key: String, value: String?) {
    if (value != null) GameDetailRow(key = key, value = value)
    else GameDetailRow(key = key, value = "Unknown")
}

@Composable
private fun GameDetailRow(key: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = key, fontWeight = FontWeight.Bold)
        VerticalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value)
    }
}