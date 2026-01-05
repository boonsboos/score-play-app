package nl.connectplay.scoreplay.screens.session

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.session.PlayerUi
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.session.PlayerRow
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.AddPlayerDialog
import nl.connectplay.scoreplay.ui.components.session.SessionTabs
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    backStack: NavBackStack<NavKey>,
) {
    // Use the Activity as ViewModelStoreOwner so the same SessionViewModel instance is shared across session screens.
    val activity = LocalContext.current as ComponentActivity
    val sessionViewModel: SessionViewModel = koinViewModel(viewModelStoreOwner = activity)

    // Collect UI state (recomposes on changes).
    val state by sessionViewModel.state.collectAsState()
    val onEvent = sessionViewModel::onEvent

    val tokenStore: TokenDataStore = koinInject()
    val userId by tokenStore.userId.collectAsState(null)

    // Initialize the session once we know the userId (ensures owner player is present).
    LaunchedEffect(userId) {
        userId?.let {
            sessionViewModel.onEvent(
                SessionEvent.Initialize(it)
            )
        }
    }

    // External data sources required for setup UI.
    val games by sessionViewModel.games.collectAsState()
    val friends by sessionViewModel.friends.collectAsState()
    val loading by sessionViewModel.loading.collectAsState()

    /** UI States */

    // Games dropdown state (search + expand/collapse).
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val selectedGame = remember(state.gameId, games) {
        games.firstOrNull { it.id == state.gameId }
    }

    // Dialog visibility is purely UI state (not persisted).
    var showAddPlayerDialog by remember { mutableStateOf(false) }

    // Filter games client-side based on search input.
    val filteredGames = remember(games, searchQuery) {
        if (searchQuery.isBlank()) games
        else games.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // When a game is selected, reflect it in the search field unless the user is actively interacting.
    LaunchedEffect(selectedGame?.id) {
        if (selectedGame != null && !expanded) {
            searchQuery = selectedGame.name
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session", backStack = backStack) },
        floatingActionButton = {
            // Persist the draft session to Room before navigating to scoring.
            FloatingActionButton(onClick = {
                onEvent(SessionEvent.SaveSession)
                backStack.add(Screens.SessionScore)
            }) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Score Screen") }
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
                onExpandedChange = { expanded = !expanded },
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
                        // Note: "loading" refers to fetching games/friends; this UI uses it mainly for games.
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
                                            checked = state.gameId == game.id,
                                            onCheckedChange = { checked ->
                                                if (checked) {
                                                    onEvent(SessionEvent.SetGame(game.id))
                                                    searchQuery = game.name
                                                    expanded = false
                                                }
                                            }
                                        )
                                    },
                                    onClick = {
                                        onEvent(SessionEvent.SetGame(game.id))
                                        searchQuery = game.name
                                        expanded = false
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
                            text = selectedGame.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = selectedGame.description,
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

            // Render the current player list; owner ("You") cannot be removed.
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

        /** ADD PLAYER DIALOG */
        if (showAddPlayerDialog) {
            AddPlayerDialog(
                friends = friends,
                onDismiss = { showAddPlayerDialog = false },

                // Adds an existing friend (backend user) as a session player.
                onAddFriend = { userId, guestName ->
                    onEvent(SessionEvent.AddPlayer(userId, guestName))
                    showAddPlayerDialog = false
                },

                // Adds a guest name; guests are stored with the owner's userId + guestName for identity.
                onAddGuest = { guestName ->
                    val ownerUserId = state.userId
                    if (ownerUserId != null) {
                        onEvent(SessionEvent.AddPlayer(userId = ownerUserId, guestName = guestName))
                        showAddPlayerDialog = false
                    }
                }
            )
        }
    }
}