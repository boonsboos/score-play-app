package nl.connectplay.scoreplay.screens.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.RoundScoreRow
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SessionFinishScreen(
    backStack: NavBackStack<NavKey>,
    sessionViewModel: SessionViewModel = koinViewModel()
) {
    val state by sessionViewModel.state.collectAsState()
    val onEvent = sessionViewModel::onEvent

    LaunchedEffect(Unit) {
        sessionViewModel.loadActiveSessionFromDb(computeWinner = true)
    }

    val winnerName = state.winnerPlayer?.let { it.guestName ?: "You" }
    val winnerScore = state.winnerScore ?: 0.0

    var selectedVisibility by remember { mutableStateOf(state.visibility) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Session", backStack = backStack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Upload Session to Leaderboard */ },
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Upload"
                )
                Text("Upload")
            }
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
                text = "The winner is... $winnerName!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))


            RoundScoreRow(
                name = winnerName.toString(),
                score = winnerScore
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Session Visibility",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.height(8.dp))

            SingleChoiceSegmentedButtonRow(modifier = Modifier) {
                SessionVisibility.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = selectedVisibility == option,
                        onClick = { selectedVisibility = option },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = SessionVisibility.entries.size
                        )
                    ) {
                        Text(
                            text = option.toLabel(),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "End of Session picture (optional)",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = { /* TODO: pick image */ },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload")
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Upload a picture of the table at the end of the game.",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}