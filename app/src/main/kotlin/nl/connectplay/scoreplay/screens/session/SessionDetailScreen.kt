package nl.connectplay.scoreplay.screens.session

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.session.SessionDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SessionDetailScreen(
    backStack: NavBackStack<NavKey>,
    sessionId: String,
    targetId: Int,
    sessionViewModel: SessionDetailViewModel = koinViewModel()
    ) {

    LaunchedEffect(sessionId) {
        sessionViewModel.handleFetch(targetId, sessionId)
    }

    val state by sessionViewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

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
    val game = session.game

    Scaffold(
        topBar = { ScorePlayTopBar(title = "Session Detail", backStack = backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                FallbackImage(
                    url = game.pictures.firstOrNull(),
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

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text("Session ID: ${session.sessionId}")
                Text("Game: ${game.name}")
                Text("Host ID: ${session.hostId}")
                Text("Visibility: ${session.visibility}")
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            ScorePlayButton(
                label = "Delete Session",
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDeleteDialog = true }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Session") },
                text = {
                    Text(
                        "Are you sure you want to delete this session? This action cannot be undone."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            sessionViewModel.handleDelete()
                            showDeleteDialog = false
                            backStack.apply {
                                if (isNotEmpty()) removeLast()
                            }
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
