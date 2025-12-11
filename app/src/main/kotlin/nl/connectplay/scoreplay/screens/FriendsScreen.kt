package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.Friend
import nl.connectplay.scoreplay.models.FriendRequest
import nl.connectplay.scoreplay.viewModels.FriendViewModel
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.CircleAvatar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun FriendsScreen(
    backStack: NavBackStack<NavKey>,
    friendViewModel: FriendViewModel = koinViewModel()
) {
    val uiState by friendViewModel.uiState.collectAsState()
    val friends = uiState.friends
    val friendRequests = uiState.friendRequests

    Scaffold(
        topBar = { ScorePlayTopBar(title = "Friends", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.onSecondary)
        ) {
            FriendList(friendRequests, friends, friendViewModel)
        }
    }
}

@Composable
fun FriendList(
    friendRequests: List<FriendRequest>,
    friends: List<Friend>,
    viewModel: FriendViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        if (friendRequests.isNotEmpty()) {
            item {
                Text(
                    "Friend Requests",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            items(friendRequests) { request ->
                FriendRequestRow(
                    request = request,
                    onAccept = { viewModel.approveRequest(request.id) },
                    onDecline = { viewModel.declineRequest(request.id) }
                )
            }
        }

        if (friends.isNotEmpty()) {
            item {
                Text(
                    "Friends",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            items(friends) { friend ->
                FriendRow(friend)
            }
        } else {
            item {
                Text(
                    "No friends found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}

@Composable
fun FriendRow(friend: Friend) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            CircleAvatar(friend.avatarLetter)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                friend.username,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FriendRequestRow(
    request: FriendRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                CircleAvatar(request.avatarLetter)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    request.username,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    IconButton(
                        onClick = onAccept,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Accept",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Decline",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}