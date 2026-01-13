package nl.connectplay.scoreplay.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.datetime.format
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.LoadingSection
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.utilities.formatted
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * UserSessionsSceen that shows all played sessions of a user
 *
 * This screen is opened from the Profilescreen by clicking "View all".
 * All user sessions are loaded using loadAllSessions().
 *
 * @param backStack Navigation stack
 * @param targetUserId User whose sessions are shown (null = current user)
 */
@Composable
fun UserSessionsScreen(
    backStack: NavBackStack<NavKey>,
    targetUserId: Int?,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(
        parameters = { parametersOf(targetUserId) }
    ),
    tokenStore: TokenDataStore = koinInject()
) {
    val userId by tokenStore.userId.collectAsState(null)
    // observes the profile data (id, username, email)
    val profileState by profileViewModel.profileState.collectAsState()
    val sessionsState by profileViewModel.sessionsState.collectAsState()
    val title = when (val state = profileState) {
        is UiState.Success -> {
            if (targetUserId != null && targetUserId != userId) {
                stringResource(R.string.screen_sessions_target_title, state.data.username)
            } else {
                stringResource(R.string.screen_sessions_title)
            }
        }

        else -> ""
    }

    // load all the sessions when the profile is loaded
    LaunchedEffect(profileState) {
        val state = profileState
        if (state is UiState.Success) {
            profileViewModel.loadAllSessions(state.data.id)
        }
    }

    val ownerName: String = (profileState as? UiState.Success)
        ?.data?.username
        ?: "Session Owner"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = title, backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        // scrollable session list
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (sessionsState) {

                // show the loading spinner while data is loading
                UiState.Loading -> {
                    item { LoadingSection() }
                }

                is UiState.Error -> {
                    item {
                        Text(
                            text = stringResource(R.string.session_list_error),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // shows the list of sessions when data is loaded
                is UiState.Success -> {
                    val sessions = (sessionsState as UiState.Success).data

                    if (sessions.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.session_list_empty),
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(sessions) { session ->
                            ListItem(
                                modifier = Modifier.clickable {
                                    backStack.add(
                                        Screens.SessionDetail(
                                            sessionId = session.id,
                                            userId = session.hostId,
                                            ownerName = ownerName
                                        )
                                    )
                                },
                                headlineContent = {
                                    Text(session.game.name)
                                },
                                supportingContent = {
                                    Text(
                                        stringResource(
                                            R.string.session_screen_played_on,
                                            session.startTime.formatted()
                                        )
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        modifier = Modifier.size(48.dp),
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = "Session Game Image",
                                    )
                                },
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                )
                            )
                        }
                    }
                }

                else -> Unit
            }
        }
    }
}
