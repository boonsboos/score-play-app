package nl.connectplay.scoreplay.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.LoadingSection
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun FollowedGamesScreen(
    backStack: NavBackStack<NavKey>,
    targetUserId: Int?,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(parameters = { parametersOf(targetUserId) }),
    tokenStore: TokenDataStore = koinInject()
) {
    val userId by tokenStore.userId.collectAsState(null)
    val profileState by profileViewModel.profileState.collectAsState()
    val gamesState by profileViewModel.gamesState.collectAsState()

    val title = when (val state = profileState) {
        is UiState.Success -> {
            if (targetUserId != null && targetUserId != userId) {
                stringResource(R.string.screen_followed_games_target_title, state.data.username)
            } else stringResource(R.string.screen_followed_games_title)
        }

        else -> ""
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title, backStack) },
        bottomBar = { BottomNavBar(backStack) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (gamesState) {
                UiState.Loading -> {
                    item { LoadingSection() }
                }

                is UiState.Error -> {
                    item {
                        Text(
                            text = stringResource(
                                R.string.followed_games_error,
                                (gamesState as UiState.Error).exception?.message
                                    ?: stringResource(R.string.unknown_error)
                            ),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.errorContainer)
                                .padding(16.dp)
                        )
                    }
                }

                is UiState.Success -> {
                    val games = (gamesState as UiState.Success).data
                    if (games.isNotEmpty())
                        items(games) {
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        backStack.add(Screens.GameDetail(it.id))
                                    },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                headlineContent = { Text(it.name) },
                                overlineContent = { Text(it.publisher) },
                                supportingContent = {
                                    Text(
                                        it.description,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                leadingContent = { Icon(Icons.Filled.Image, "TODO image") }
                            )
                        }
                }

                else -> Unit
            }
        }
    }
}