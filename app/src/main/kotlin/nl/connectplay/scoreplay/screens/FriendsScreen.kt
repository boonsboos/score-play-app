package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.models.friends.FriendRequestListResponse
import nl.connectplay.scoreplay.models.friends.UserFriend
import nl.connectplay.scoreplay.viewModels.FriendViewModel
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.CircleAvatar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import org.koin.androidx.compose.koinViewModel
import nl.connectplay.scoreplay.ui.components.LoadingSection

@Composable
fun FriendsScreen(
    backStack: NavBackStack<NavKey>,
    friendViewModel: FriendViewModel = koinViewModel()
) {
    val uiState by friendViewModel.uiState.collectAsState()

    val error by friendViewModel.errors.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        error.message?.let { snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short) }
    }

    Scaffold(
        topBar = {
            ScorePlayTopBar(
                title = stringResource(R.string.screen_friends_title),
                backStack = backStack
            )
        },
        bottomBar = { BottomNavBar(backStack) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                LoadingSection()
            } else {
                FriendList(
                    friendRequests = uiState.friendRequests,
                    friends = uiState.friends,
                    viewModel = friendViewModel,
                    backStack = backStack
                )
            }
        }
    }
}

@Composable
fun FriendList(
    friendRequests: FriendRequestListResponse,
    friends: List<UserFriend>,
    viewModel: FriendViewModel,
    backStack: NavBackStack<NavKey>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (friendRequests.pending.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.friends_pending_requests),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(friendRequests.pending) { request ->
                PendingFriendRequestRow(
                    request = request,
                    onAccept = { viewModel.approveRequest(request.user.id) },
                    onDecline = { viewModel.declineRequest(request.user.id) },
                    onProfileClick = { backStack.add(Screens.Profile(request.user.id)) }
                )
                if (friendRequests.pending.size > 1 && request != friendRequests.pending.last()) {
                    HorizontalDivider()
                }
            }
        }

        if (friendRequests.outstanding.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.friends_outstanding_requests),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(friendRequests.outstanding) { request ->
                OutstandingFriendRequestRow(
                    request = request,
                    onProfileClick = { backStack.add(Screens.Profile(request.user.id)) }
                )
                if (friendRequests.outstanding.size > 1 && request != friendRequests.outstanding.last()) {
                    HorizontalDivider()
                }
            }
        }

        if (friends.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.screen_friends_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(friends) { friend ->
                FriendRow(
                    friend,
                    onProfileClick = { backStack.add(Screens.Profile(friend.user.id)) }
                )
                if (friends.size > 1 && friend != friends.last()) {
                    HorizontalDivider()
                }
            }
        } else {
            item {
                Text(
                    stringResource(R.string.friends_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun FriendRow(friend: UserFriend, onProfileClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            headlineColor = MaterialTheme.colorScheme.onSurface

        ),
        headlineContent = {
            Text(
                friend.user.username,
                fontWeight = FontWeight.Medium,
            )
        },
        leadingContent = {
            CircleAvatar(friend)
        }
    )
}

@Composable
fun PendingFriendRequestRow(
    request: UserFriend,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onProfileClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            headlineColor = MaterialTheme.colorScheme.onSurface
        ),
        headlineContent = {
            Text(
                text = request.user.username,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        supportingContent = {
            Text(
                text = stringResource(R.string.wants_to_be_your_friend),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            CircleAvatar(request)
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = CircleShape,
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Accept",
                    )
                }

                IconButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = CircleShape,
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Decline",
                    )
                }
            }
        }
    )
}

@Composable
fun OutstandingFriendRequestRow(request: UserFriend, onProfileClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProfileClick() },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            headlineColor = MaterialTheme.colorScheme.onSurface

        ),
        headlineContent = {
            Text(
                request.user.username,
                fontWeight = FontWeight.Medium,
            )
        },
        supportingContent = {
            Text(stringResource(R.string.friends_awaiting_response))
        },
        leadingContent = {
            CircleAvatar(request)
        }
    )
}
