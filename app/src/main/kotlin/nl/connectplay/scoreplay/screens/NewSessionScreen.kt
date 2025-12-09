package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.SessionEvent
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GamesListViewModel
import org.koin.androidx.compose.koinViewModel
import nl.connectplay.scoreplay.viewModels.SessionState
import nl.connectplay.scoreplay.viewModels.SessionViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionScreen(
    backStack: NavBackStack<NavKey>,
    state: SessionState,
    onEvent: (SessionEvent) -> Unit,
    viewModel: GamesListViewModel = koinViewModel()
) {
    val sessionViewModel: SessionViewModel = koinViewModel()
    val state by sessionViewModel.state.collectAsState()

    val games by viewModel.gamesList.collectAsState()
    val loading by viewModel.areLoading.collectAsState()

    LaunchedEffect(Unit) { if (games.isEmpty() && !loading) { viewModel.fetch() } }

    LaunchedEffect(Unit) {
        onEvent(SessionEvent.SetUser(1234))
    }

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    val filteredGames = remember(games, searchQuery) {
        if (searchQuery.isBlank()) games
        else games.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session") },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(SessionEvent.showScores)
            }) { Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Score Screen") }
        },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Choose a Game to play",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded},
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        expanded = true
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    label = { Text("Search game") },
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded && filteredGames.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    filteredGames.forEach { game ->
                        DropdownMenuItem(
                            text = { Text(game.name) },
                            leadingIcon = { Icon(Icons.Default.Image, null) },
                            trailingIcon = {
                                Checkbox(
                                    checked = selectedGame?.id == game.id,
                                    onCheckedChange = { checked ->
                                        selectedGame = if (checked) game else null
                                    }
                                )
                            },
                            onClick = {
                                selectedGame = game
                                searchQuery = game.name
                                expanded = false

                                onEvent(SessionEvent.SetGame(game.id))
                            }
                        )
                    }
                }
            }
            if (selectedGame != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = selectedGame!!.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = selectedGame!!.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    onEvent(SessionEvent.SaveSession)
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Save Session")
                }
            }

        }
    }
}