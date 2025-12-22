package nl.connectplay.scoreplay.screens.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.session.PlayerRow
import nl.connectplay.scoreplay.ui.components.session.PlayerUi
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.SessionTabs
import nl.connectplay.scoreplay.viewModels.session.SessionState
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    backStack: NavBackStack<NavKey>,
    state: SessionState,
    onEvent: (SessionEvent) -> Unit,
    sessionViewModel: SessionViewModel = koinViewModel()
) {
    val games by sessionViewModel.games.collectAsState()
    val loading by sessionViewModel.loading.collectAsState()

    /** UI States */

    // Related to Games
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }

    // Related to Players
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var isFriendMode by remember { mutableStateOf(true) }
    var newPlayerName by remember { mutableStateOf("") }
    var selectedFriendId by remember { mutableStateOf<Int?>(null) }

    /** @TODO REMOVE MOCK FRIENDS LATER */
    val mockFriends = remember {
        listOf(
            PlayerUi(id = 101, name = "Alice", isCurrentUser = false),
            PlayerUi(id = 102, name = "Bob", isCurrentUser = false),
            PlayerUi(id = 103, name = "Charlie", isCurrentUser = false)
        )
    }

    val filteredGames = remember(games, searchQuery) {
        if (searchQuery.isBlank()) games
        else games.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session", backStack = backStack) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(SessionEvent.SaveSession)
                backStack.add(Screens.SessionScore)
            }) { Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Score Screen") }
        },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

            SessionTabs(
                backStack = backStack,
                currentScreen = Screens.SessionSetup
            )

            Spacer(modifier = Modifier.height(16.dp))

            /** GAMES */
            Text(
                text = "1. Choose a Game to play",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        expanded = true
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .padding(horizontal = 8.dp)
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
                    when {
                        loading -> {
                            DropdownMenuItem(
                                text = { Text("Loading games…") },
                                onClick = {},
                                enabled = false
                            )
                        }

                        filteredGames.isEmpty() -> {
                            DropdownMenuItem(
                                text = { Text("No games found") },
                                onClick = {},
                                enabled = false
                            )
                        }

                        else -> {
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

                                        onEvent(
                                            SessionEvent.SetGame(game.id)
                                        )
                                    }
                                )
                            }
                        }
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
                            text = selectedGame?.name ?: "",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = selectedGame?.description ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /** PLAYERS */
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Who's playing?",
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = {
                        showAddPlayerDialog = true
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add player",
                    )
                    Text("Add player")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            state.sessionPlayers.forEach { player ->
                val isOwner = player.userId == state.userId && player.guestName == null

                PlayerRow(
                    player = PlayerUi(
                        id = player.userId,
                        name = if (isOwner) "You" else player.guestName.orEmpty(),
                        isCurrentUser = isOwner
                    ),
                    onRemove = {
                        if (!isOwner) {
                            onEvent(
                                SessionEvent.RemovePlayer(
                                    userId = player.userId,
                                    guestName = player.guestName
                                )
                            )
                        }
                    }
                )
            }
        }

        /** DIALOG */
        if (showAddPlayerDialog) {
            AlertDialog(
                onDismissRequest = { showAddPlayerDialog = false },
                title = {
                    Text(text = "Add player")
                },
                text = {
                    Column {
                        // Mode switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(
                                onClick = {
                                    isFriendMode = true
                                    newPlayerName = ""
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (isFriendMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Friend")
                            }

                            TextButton(
                                onClick = {
                                    isFriendMode = false
                                    selectedFriendId = null
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (!isFriendMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Text("Guest")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isFriendMode) {
                            mockFriends.forEach { friend ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedFriendId == friend.id,
                                        onCheckedChange = {
                                            selectedFriendId =
                                                if (it) friend.id else null
                                        }
                                    )
                                    Text(friend.name)
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = newPlayerName,
                                onValueChange = { newPlayerName = it },
                                label = { Text("Player name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = if (isFriendMode)
                            selectedFriendId != null
                        else
                            newPlayerName.isNotBlank(),
                        onClick = {
                            if (isFriendMode) {
                                val friend =
                                    mockFriends.first { it.id == selectedFriendId }
                                onEvent(
                                    SessionEvent.AddPlayer(friend.id, friend.name)
                                )
                            } else {
                                val userId = state.userId
                                if (userId != null) {
                                    onEvent(
                                        SessionEvent.AddPlayer(
                                            userId = state.userId,
                                            guestName = newPlayerName.trim()
                                        )
                                    )
                                }
                            }

                            newPlayerName = ""
                            selectedFriendId = null
                            showAddPlayerDialog = false
                        }
                    ) { Text("Add") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddPlayerDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

    }
}