package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GameDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val gameDetail: GameDetailViewModel = koinViewModel()
    val state = gameDetail.uiState.collectAsState()
    val loading = gameDetail.loadingState.collectAsState()

    val carouselState = rememberCarouselState{ state.value.imageUrls.size }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(gameId) {
        gameDetail.getGame(gameId = gameId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = ""/* TODO Game title */, backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box {

                if (loading.value) {
                    // TODO: show loading image
                    return@Box
                }

                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = 200.dp,
                    itemSpacing = 10.dp
                ) { index ->
                    // TODO: show game pictures

                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(state.value.imageUrls[index])
                            .build(),
                        contentDescription = "A picture of ${state.value.gameData?.name ?: "an unknown game"}"
                    )
                }
            }
            // button group
            Row {
                // Start a new session
                FilledIconButton(onClick = {
                    // TODO: new session
                }) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "New session")
                }

                // Redirect to show leaderboard
                Button(onClick = {
                    // TODO: show leaderboard
                }) {
                    Icon(imageVector = Icons.Default.StackedBarChart, contentDescription = "Leaderboard")
                    Text("Leaderboard")
                }

                // Bookmark toggle button
                OutlinedIconToggleButton(
                    checked = state.value.gameFollowed,
                    onCheckedChange = {
                        coroutineScope.launch {
                            gameDetail.toggleFollow()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Follow game"
                    )
                }

                // Game edit button
                OutlinedIconButton(onClick = {
                    // TODO: game edit screen
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}