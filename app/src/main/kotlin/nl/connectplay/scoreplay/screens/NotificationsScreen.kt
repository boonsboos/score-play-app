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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
    notificationViewModel: NotificationListViewModel = koinViewModel()
) {
    val notifications by notificationViewModel.state.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                is HighscoreEvent -> backStack.add(Screens.Leaderboard(gameId = event.gameId))
                is FriendRequestEvent -> backStack.add(Screens.Profile(userId = event.targetUserId))
                is FriendRequestReplyEvent -> backStack.add(Screens.Friends)
            }

            // mark as read after the user clicked the notification
            notificationViewModel.markNotificationsAsRead(notification.notificationId)

            // clear selection so this block wouldnt trigger again
            selectedNotification.value = null
        }
    }
}

// notifications row layout for a notification item
@Composable
fun NotificationRow(
    read: Boolean,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (read)
                    MaterialTheme.colorScheme.surfaceContainerLowest
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun FriendRequestNotificationItem(
    event: FriendRequestEvent,
    read: Boolean,
    onClick: () -> Unit,
) {
    NotificationRow(read, onClick) {
        Icon(Icons.Filled.Person, null)
        Spacer(Modifier.width(12.dp))
        Text("Friend request • ${event.createdFormatted(read)}")
    }
}

@Composable
fun FriendRequestReplyNotificationItem(
    event: FriendRequestReplyEvent,
    read: Boolean,
    onClick: () -> Unit,
) {
    val text =
        if (event.accepts) "Friend request accepted"
        else "Friend request declined"

    NotificationRow(read, onClick) {
        Icon(Icons.Filled.Person, null)
        Spacer(Modifier.width(12.dp))
        Text("$text • ${event.createdFormatted(read)}")
    }
}

@Composable
fun HighscoreNotificationItem(
    event: HighscoreEvent,
    read: Boolean,
    onClick: () -> Unit,
) {
    NotificationRow(read, onClick) {
        Icon(Icons.Filled.EmojiEvents, null)
        Spacer(Modifier.width(12.dp))
        Text("New highscore • ${event.createdFormatted(read)}")
    }
}


// converts the date to a displayfriendly format
@OptIn(ExperimentalTime::class)
fun BaseEvent.createdFormatted(read: Boolean): String {
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