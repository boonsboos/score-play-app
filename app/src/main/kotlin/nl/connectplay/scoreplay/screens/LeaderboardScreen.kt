package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.utilities.formattedDate
import nl.connectplay.scoreplay.viewModels.LeaderboardViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val Gold = 0xFFFFD700
private const val Silver = 0xFFC0C0C0
private const val Bronze = 0xFFCE8946

@Composable
fun LeaderboardScreen(
    gameId: Int,
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    leaderboardViewModel: LeaderboardViewModel = koinViewModel(parameters = { parametersOf(gameId) })
) {
    val listState = rememberLazyListState()

    val scores by leaderboardViewModel.scores.collectAsState()
    val loading by leaderboardViewModel.loading.collectAsState()

    if (loading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Leaderboard", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        LazyColumn(state = listState, modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            itemsIndexed(items = scores) { index, score ->
                val iconColor = Color( when(index) {
                    0 -> Gold
                    1 -> Silver
                    2 -> Bronze
                    else -> 0xFF000000
                })

                ListItem(
                    leadingContent = {
                        val requiredSpaceModifier = Modifier.requiredWidth(40.dp)

                        if (index < 3) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = iconColor,
                                modifier = requiredSpaceModifier.height(40.dp)
                            )
                        } else {
                            Text(
                                text = "#${index+1}",
                                modifier = requiredSpaceModifier,
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    headlineContent = {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(score.playerName)
                            Text(score.score.toString())
                        }
                    },
                    overlineContent = { Text(score.achievedAt.formattedDate())},
                    modifier = Modifier
                        .fillMaxWidth(),
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
            }
        }
    }
}
