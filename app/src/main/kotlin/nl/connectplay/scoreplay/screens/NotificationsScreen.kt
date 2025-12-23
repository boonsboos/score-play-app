package nl.connectplay.scoreplay.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.NotificationListViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import nl.connectplay.scoreplay.models.notifications.NotificationFilter
import nl.connectplay.scoreplay.models.notifications.NotificationUi
import nl.connectplay.scoreplay.models.notifications.events.BaseEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestReplyEvent
import nl.connectplay.scoreplay.models.notifications.events.HighscoreEvent
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.ui.components.FilterButton
import kotlin.time.ExperimentalTime

/**
 * Screen that displays all notifications for the user.
 *
 * This screen shows a list of notifications
 * - allows filtering by type
 * - redirects when a notification is clicked
 */
@Composable
fun NotificationsScreen(
    backStack: NavBackStack<NavKey>,
    notificationViewModel: NotificationListViewModel = koinViewModel(),
) {
    val notifications by notificationViewModel.state.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()
    val requiredHighScoreUsers by notificationViewModel.highscoreEventUsers.collectAsState()

    val listState = rememberLazyListState()
    val filterScrollState = rememberScrollState()
    val selectedNotification =
        remember { mutableStateOf<NotificationUi?>(null) } // holds the selected notification


    Scaffold(
        modifier = Modifier.fillMaxSize(), // the screen is filled entire size
        topBar = {
            ScorePlayTopBar(
                title = "Notifications",
                backStack = backStack
            )
        }, // added the composable topbar
        // added the composable bottombar
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // horizontal scrollbar for the filterbuttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(filterScrollState)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .height(40.dp), // make sure the notifications aren't clipping the filter buttons
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterButton(
                    title = "All",
                    // mark this filter button as selected when the ALL filter is active (also for styling)
                    selected = notificationViewModel.filter.collectAsState().value == NotificationFilter.ALL,
                    // change the filter state so the ViewModel knows which filter is active
                    onClick = {
                        notificationViewModel.setFilter(NotificationFilter.ALL)
                    }
                )
                FilterButton(
                    title = "Unread",
                    selected = notificationViewModel.filter.collectAsState().value == NotificationFilter.UNREAD,
                    onClick = {
                        notificationViewModel.setFilter(NotificationFilter.UNREAD)
                    }
                )
                FilterButton(
                    title = "Friend Requests",
                    selected = notificationViewModel.filter.collectAsState().value == NotificationFilter.FRIEND_REQUEST,
                    onClick = {
                        notificationViewModel.setFilter(NotificationFilter.FRIEND_REQUEST)
                    }
                )
                FilterButton(
                    title = "Replies",
                    selected = notificationViewModel.filter.collectAsState().value == NotificationFilter.REPLIES,
                    onClick = {
                        notificationViewModel.setFilter(NotificationFilter.REPLIES)
                    }
                )
                FilterButton(
                    title = "Highscores",
                    selected = notificationViewModel.filter.collectAsState().value == NotificationFilter.HIGHSCORES,
                    onClick = {
                        notificationViewModel.setFilter(NotificationFilter.HIGHSCORES)
                    }
                )
            }
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                notifications.isEmpty() -> {
                    Text(
                        text = "No notifications available", textAlign = TextAlign.Center
                    )
                }

                else -> {
                    // this makes a scrollable list of notifications
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications) { notification ->
                            // checks the type of the event
                            when (val event = notification.event) {

                                is FriendRequestEvent ->
                                    FriendRequestNotificationItem(
                                        event = event,
                                        read = notification.read,
                                        onClick = {
                                            Log.d(
                                                "NOTIFY",
                                                "Clicked notification: ${notification.notificationId}"
                                            )
                                            selectedNotification.value = notification
                                        }
                                    )

                                is FriendRequestReplyEvent ->
                                    FriendRequestReplyNotificationItem(
                                        event = event,
                                        read = notification.read,
                                        onClick = {
                                            Log.d(
                                                "NOTIFY",
                                                "Clicked notification: ${notification.notificationId}"
                                            )
                                            selectedNotification.value = notification
                                        }
                                    )

                                is HighscoreEvent ->
                                    HighscoreNotificationItem(
                                        event = event,
                                        read = notification.read,
                                        onClick = {
                                            Log.d(
                                                "NOTIFY",
                                                "Clicked notification: ${notification.notificationId}"
                                            )
                                            selectedNotification.value = notification
                                        },
                                        userDto = if (event.score.sessionPlayer.guest == null) {
                                            requiredHighScoreUsers[event.score.sessionPlayer.userId]
                                        } else {
                                            null
                                        }
                                    )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    // mark notification as read after selection and navigate to the event
    selectedNotification.value?.let { notification ->
        LaunchedEffect(notification.notificationId) {
            when (val event = notification.event) {
                is HighscoreEvent -> backStack.add(Screens.Leaderboard(gameId = event.game.id))
                is FriendRequestEvent -> backStack.add(Screens.Profile(userId = event.from.id))
                is FriendRequestReplyEvent -> backStack.add(Screens.Friends)
            }

            // mark as read after the user clicked the notification
            notificationViewModel.markNotificationsAsRead(notification.notificationId)

            // clear selection so this block wouldnt trigger again
            selectedNotification.value = null
        }
    }
}

@Composable
fun NotificationRow(
    event: BaseEvent,
    read: Boolean,
    onClick: () -> Unit,
    leadingContent: @Composable () -> Unit,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable () -> Unit
) {
    ListItem(
        leadingContent = leadingContent,
        overlineContent = { Text(event.formatCreatedTime()) },
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors()
            .copy(containerColor = if (read)
                MaterialTheme.colorScheme.surfaceContainerLowest
            else
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    )
}

@Composable
fun FriendRequestNotificationItem(
    event: FriendRequestEvent,
    read: Boolean,
    onClick: () -> Unit,
) {
    NotificationRow(
        event = event,
        read = read,
        onClick = onClick,
        leadingContent = { Icon(Icons.Filled.Person, null) },
        headlineContent = { Text("New friend request from ${event.from.username}") },
        supportingContent = { Text("Will you accept?") },
    )
}

@Composable
fun FriendRequestReplyNotificationItem(
    event: FriendRequestReplyEvent,
    read: Boolean,
    onClick: () -> Unit,
) {
    NotificationRow(
        event = event,
        read = read,
        onClick = onClick,
        leadingContent = { Icon(Icons.Filled.Person, null) },
        headlineContent = { Text("${event.respondingUser.username} responded!") },
        supportingContent = {
            if (event.accepts) {
                Text("${event.respondingUser.username} accepted your friend request! Friends forever!")
            } else {
                Text("${event.respondingUser.username} declined your friend request... :(")
            }
        }
    )
}

@Composable
fun HighscoreNotificationItem(
    event: HighscoreEvent,
    read: Boolean,
    onClick: () -> Unit,
    userDto: UserProfile?
) {
    NotificationRow(
        event = event,
        read = read,
        onClick = onClick,
        headlineContent = { Text("New highscore on ${event.game.name}") },
        leadingContent = { Icon(Icons.Filled.EmojiEvents, null) },
        supportingContent = {
            if (userDto != null) {
                Text("${userDto.username} got the #${event.podium} score with ${event.score.score}")
            } else {
                Text("${event.score.sessionPlayer.guest} got the #${event.podium} score with ${event.score.score}")
            }
        }
    )
}

// converts the date to a displayfriendly format
@OptIn(ExperimentalTime::class)
fun BaseEvent.formatCreatedTime(): String {
    val formatted = created
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(
            LocalDateTime.Format {
                day()
                char('-')
                monthNumber()
                char('-')
                year()
                char(' ')
                hour()
                char(':')
                minute()
            }
        )
    return formatted
}