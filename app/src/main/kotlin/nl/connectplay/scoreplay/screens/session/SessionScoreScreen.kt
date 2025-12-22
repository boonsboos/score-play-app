package nl.connectplay.scoreplay.screens.session

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.AddRoundDialog
import nl.connectplay.scoreplay.ui.components.session.SessionTabs
import nl.connectplay.scoreplay.ui.components.session.SpeedDial
import nl.connectplay.scoreplay.ui.components.session.SpeedDialAction
import nl.connectplay.scoreplay.viewModels.session.SessionState

@Composable
fun SessionScoreScreen(
    backStack: NavBackStack<NavKey>,
    state: SessionState,
    onEvent: (SessionEvent) -> Unit,
) {
    var showNewRoundDialog by remember { mutableStateOf((false)) }

    Log.d(
        "SessionScoreScreen",
        "state = $state"
    )

    val actions = listOf(
        SpeedDialAction(
            label = "Finish",
            icon = Icons.Default.Check,
            onClick = { /* Finish */ }
        ),
        SpeedDialAction(
            label = "New Round",
            icon = Icons.Default.Add,
            onClick = { showNewRoundDialog = true }
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session", backStack = backStack) },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Back FAB - bottom left
                FloatingActionButton(
                    onClick = { backStack.add(Screens.SessionSetup) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Setup Screen"
                    )
                }

                // SpeedDial — bottom right
                SpeedDial(
                    actions = actions,
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
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
                currentScreen = Screens.SessionScore
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "There's no rounds yet!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Start a round to record your scores!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showNewRoundDialog && state.sessionPlayers.isNotEmpty()) {
            AddRoundDialog(
                sessionId = state.roomSession?.id ?: return,
                gameId = state.roomSession.gameId,
                players = state.sessionPlayers,
                onDismiss = { showNewRoundDialog = false },
                onSave = { scores ->
                    //viewModel.addRound(scores)
                    showNewRoundDialog = false
                }
            )

    }
}