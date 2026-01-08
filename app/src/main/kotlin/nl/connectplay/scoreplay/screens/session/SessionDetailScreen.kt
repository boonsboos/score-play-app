package nl.connectplay.scoreplay.screens.session

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.session.SessionDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SessionDetailScreen(
    backStack: NavBackStack<NavKey>,
    sessionId: String,
    userId: Int,
    ownerName: String,
    sessionViewModel: SessionDetailViewModel = koinViewModel()
) {

    LaunchedEffect(sessionId) {
        sessionViewModel.handleFetch(userId, sessionId)
    }

    val state by sessionViewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val tokenStore: TokenDataStore = koinInject()
    val currentUserId by tokenStore.userId.collectAsState(null)

    if (state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LinearProgressIndicator()
            Spacer(modifier = Modifier.height(12.dp))
            Text("Loading…")
        }
        return
    }

    val session = state.session

    if (session == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Session not found")
        }
        return
    }

    val scores = state.scores

    Scaffold(
        topBar = { ScorePlayTopBar(title = "Session Detail", backStack = backStack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    FallbackImage(
                        url = session.game.pictures.firstOrNull(),
                        size = 200.dp,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "Placeholder Game Image",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = session.game.name,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    if (currentUserId != null && currentUserId == userId) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { showDeleteDialog = true }
                                .padding(4.dp)
                        )
                    }
                }
            }

            if (scores.isNullOrEmpty()) {
                item {
                    Text("No scores not found")
                }
            } else {
                val rounds = scores
                    .groupBy { it.turn }
                    .toSortedMap()

                item {
                    Text(
                        text = "Rounds",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(rounds.entries.toList()) { (turn, roundScores) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        ListItem(
                            headlineContent = { Text("Round $turn") },
                            supportingContent = {
                                Column {
                                    val names = roundScores
                                        .map { it.sessionPlayer.guest ?: ownerName }
                                        .distinct()

                                    names.forEach { playerName ->
                                        val score = roundScores.first {
                                            (it.sessionPlayer.guest ?: ownerName) == playerName
                                        }.score
                                        Text("$playerName: $score")
                                    }
                                }
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                headlineColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Session") },
                text = { Text("Are you sure you want to delete this session? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            sessionViewModel.handleDelete()
                            showDeleteDialog = false
                            backStack.apply { if (isNotEmpty()) removeLast() }
                        }
                    ) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}
