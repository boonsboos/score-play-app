package nl.connectplay.scoreplay.screens.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.room.dao.SessionScoreDao
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.RoundScoreRow
import nl.connectplay.scoreplay.viewModels.profile.ProfileViewModel
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun RoundDetailScreen(
    backStack: NavBackStack<NavKey>,
    sessionId: Int,
    sessionViewModel: SessionViewModel = koinViewModel(parameters = { parametersOf(sessionId) }),
    turn: Int,
) {
    val state by sessionViewModel.state.collectAsState()
    val sessionScoreDao: SessionScoreDao = koinInject()

    val rows = sessionScoreDao.observeRoundScores(sessionId, turn)
        .collectAsState(initial = emptyList()).value

    // Make sure we have the active session (needed for sessionId)
    LaunchedEffect(Unit) {
        if (state.roomSession == null) {
            sessionViewModel.loadActiveSessionFromDb()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Round $turn", backStack = backStack) },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Back FAB - bottom left
                FloatingActionButton(
                    onClick = { backStack.add(Screens.SessionScore) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Score Screen"
                    )
                }

                FloatingActionButton(
                    onClick = { /* finish */ },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Finish")
                }
            }
        },

        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            rows.forEach { row ->
                RoundScoreRow(
                    name = row.guestName ?: "Player ${row.sessionPlayerId}",
                    score = row.score
                )
            }
        }
    }
}
