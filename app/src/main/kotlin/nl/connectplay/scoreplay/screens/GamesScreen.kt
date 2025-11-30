package nl.connectplay.scoreplay.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ErrorMessage
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GamesListViewModel
import org.koin.androidx.compose.koinViewModel

private const val GAMES_SCREEN_LOGTAG = "GamesScreen"

@Composable
fun GamesScreen(
    backStack: NavBackStack<NavKey>,
    gameListViewModel: GamesListViewModel = koinViewModel()
) {
    val gamesList by gameListViewModel.gamesList.collectAsState()
    val gamesAreLoading by gameListViewModel.areLoading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        try {
            gameListViewModel.fetch()
        } catch (e: Exception) {
            Log.e(
                GAMES_SCREEN_LOGTAG,
                "An error occurred while loading the games screen ${e.message}",
                e
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Games") },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary),
        ) {
            // Show error message if we are done loading and no games were found
            if (gamesList.isEmpty() && !gamesAreLoading) {
                return@Box ErrorMessage("No games :(")
            }

            LazyColumn() {
                items(items = gamesList, key = { it.id}) {
                    ListItem(
                        modifier = Modifier.clickable {
                            // TODO: navigate to game detail screen
                        },
                        headlineContent = { Text(it.name) },
                        overlineContent = { Text(it.publisher) },
                        supportingContent = { Text(it.description, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingContent = { Icon(Icons.Filled.Image, "TODO image")}
                    )
                }
            }

        }
    }
}