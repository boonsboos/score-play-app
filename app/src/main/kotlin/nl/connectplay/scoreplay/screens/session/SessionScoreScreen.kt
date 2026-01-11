package nl.connectplay.scoreplay.screens.session

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.AddRoundDialog
import nl.connectplay.scoreplay.ui.components.session.FinishSessionDialog
import nl.connectplay.scoreplay.ui.components.session.SessionTabs
import nl.connectplay.scoreplay.ui.components.session.SpeedDial
import nl.connectplay.scoreplay.ui.components.session.SpeedDialAction
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun SessionScoreScreen(
    backStack: NavBackStack<NavKey>,
) {
    // Share the same SessionViewModel across session screens by scoping it to the Activity.
    val activity = LocalContext.current as? ComponentActivity ?: return
    val sessionViewModel: SessionViewModel = koinViewModel(viewModelStoreOwner = activity)

    val state by sessionViewModel.state.collectAsState()
    val onEvent = sessionViewModel::onEvent

    // Load persisted session snapshot when entering this screen (only if not already in state).
    LaunchedEffect(Unit) {
        if (state.roomSession == null) {
            sessionViewModel.loadActiveSessionFromDb()
        }
    }

    // Screen-local UI state for dialogs.
    var showNewRoundDialog by remember { mutableStateOf((false)) }
    var showFinishDialog by remember { mutableStateOf((false)) }

    // SpeedDial actions: show "Finish" only when at least one round exists.
    val actions = buildList {
        if (state.turns.isNotEmpty()) {
            add(
                SpeedDialAction(
                    label = "Finish",
                    icon = Icons.Default.Check,
                    onClick = { showFinishDialog = true }
                )
            )
        }
        add(
            SpeedDialAction(
                label = "New Round",
                icon = Icons.Default.Add,
                onClick = { showNewRoundDialog = true }
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = stringResource(R.string.screen_session_title), backStack = backStack) },
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.session_score_setup_back)
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

            val session = state.roomSession

            // Empty state if there's no session yet or no rounds recorded.
            if (session == null || state.turns.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.session_score_empty),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.session_score_cta),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // List rounds; tapping navigates to round detail for that turn.
                LazyColumn {
                    items(state.turns) { turn ->
                        ListItem(
                            headlineContent = { Text(text = stringResource(R.string.session_round, turn)) },
                            trailingContent = {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                headlineColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.clickable {
                                backStack.add(
                                    Screens.RoundDetail(
                                        sessionId = session.id,
                                        turn = turn
                                    )
                                )
                            }
                        )
                    }
                }
            }

        }
    }

    // Add round dialog: only show when we have players available.
    if (showNewRoundDialog && state.sessionPlayers.isNotEmpty()) {
        AddRoundDialog(
            players = state.sessionPlayers,
            onDismiss = { showNewRoundDialog = false },
            onSave = { inputs ->
                val session = state.roomSession ?: return@AddRoundDialog
                onEvent(SessionEvent.AddRound(sessionId = session.id, gameId = session.gameId, scores = inputs))
                showNewRoundDialog = false
            }
        )
    }

    // Finish confirmation; navigates to finish screen (winner/visibility/upload).
    if (showFinishDialog) {
        FinishSessionDialog(
            completedRoundCount = state.turns.size,
            onDismiss = { showFinishDialog = false },
            onConfirm = {
                showFinishDialog = false
                backStack.add(Screens.SessionFinish)
            }
        )
    }
}