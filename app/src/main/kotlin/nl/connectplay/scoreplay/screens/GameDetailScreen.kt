package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ExpandableText
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.GameDetailViewModel
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    backStack: NavBackStack<NavKey>,
    gameDetail: GameDetailViewModel = koinViewModel(parameters = { parametersOf(gameId) })
) {
    val state by gameDetail.gameState.collectAsState()
    val loading by gameDetail.loadingState.collectAsState()

    val carouselState = rememberCarouselState { state?.pictures?.size ?: 0 }
    val snackBarState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val failedUnfollowMessage = stringResource(R.string.game_detail_failed_unfollow)
    val failedFollowMessage = stringResource(R.string.game_detail_failed_follow)

    // semantically invalid, but required within the current setup
    // TODO: refactor this so various screens do not manage the state of the session.
    val sessionViewModel: SessionViewModel = koinViewModel()

    if (loading) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
        topBar = {
            ScorePlayTopBar(
                title = state?.name ?: stringResource(R.string.game_detail_name_empty),
                backStack = backStack
            )
        },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(vertical = 6.dp)) {
                HorizontalUncontainedCarousel(
                    state = carouselState,
                    itemWidth = 300.dp,
                    itemSpacing = 10.dp
                ) { index ->
                    FallbackImage(
                        url = state?.pictures?.getOrNull(index),
                        size = 300.dp
                    ) {
                        Text("No images yet")
                    }
                }
            }

            // button group
            Row {
                // Start a new session
                FilledIconButton(onClick = {
                    backStack.add(Screens.SessionSetup(startNew = true))
                }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(R.string.new_session_button)
                    )
                }

                // Redirect to show leaderboard
                FilledTonalButton(
                    modifier = Modifier.width(200.dp),
                    onClick = {
                        backStack.add(Screens.Leaderboard(gameId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.StackedBarChart,
                        contentDescription = stringResource(R.string.screen_leaderboard_title)
                    )
                    Text(text = stringResource(R.string.screen_leaderboard_title))
                }

                // Bookmark toggle button
                OutlinedIconToggleButton(
                    checked = state?.following ?: false,
                    onCheckedChange = {
                        coroutineScope.launch {
                            val success = gameDetail.toggleFollow(gameId)
                            if (!success) {
                                snackBarState.showSnackbar(
                                    message = if (state?.following == true) {
                                        failedUnfollowMessage
                                    } else {
                                        failedFollowMessage
                                    }
                                )
                            }
                        }
                    }
                ) {
                    if (state?.following == true) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = stringResource(R.string.game_detail_cta_unfollow)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = stringResource(R.string.game_detail_cta_follow)
                        )
                    }
                }

                // Game edit button
                OutlinedIconButton(
                    onClick = {},
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 6.dp),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.game_detail_label_description),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    ExpandableText(
                        state?.description ?: stringResource(R.string.game_detail_description_empty)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .width(300.dp)
                    .padding(vertical = 6.dp),
                thickness = 1.dp
            )

            Row(modifier = Modifier.padding(horizontal = 20.dp)) {
                Column {
                    Text(
                        text = stringResource(R.string.game_detail_label_details),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    GameDetailIfPresent(
                        key = R.string.game_detail_label_publisher,
                        value =state?.publisher
                    )
                    GameDetailIfPresent(
                        key = R.string.game_detail_label_duration,
                        value = state?.duration?.let {
                            stringResource(
                                R.string.game_detail_duration_time_unit,
                                it
                            )
                        })
                    GameDetailIfPresent(
                        key = R.string.game_detail_label_players,
                        value = state?.minPlayers?.let { min ->
                            state?.maxPlayers?.let { max ->
                                if (min == max) "$min"
                                else "$min - $max"
                            }
                        })
                    GameDetailIfPresent(
                        key = R.string.game_detail_label_release_date,
                        value = state?.releaseDate?.toString()
                    )
                }
            }

        }
    }
}

@Composable
private fun GameDetailIfPresent(key: Int, value: String?) {
    if (value != null) GameDetailRow(key = key, value = value)
    else GameDetailRow(key = key, value = "Unknown")
}

@Composable
private fun GameDetailRow(key: Int, value: String, modifier: Modifier = Modifier) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = stringResource(key), fontWeight = FontWeight.Bold)
        VerticalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value)
    }
}