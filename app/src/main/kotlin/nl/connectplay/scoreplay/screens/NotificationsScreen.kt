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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import nl.connectplay.scoreplay.models.notifications.Notification
import nl.connectplay.scoreplay.models.notifications.NotificationFilter
import nl.connectplay.scoreplay.models.notifications.NotificationUi
import nl.connectplay.scoreplay.models.notifications.events.BaseEvent
import nl.connectplay.scoreplay.models.notifications.events.FriendRequestEvent
import nl.connectplay.scoreplay.ui.components.FilterButton
import kotlin.time.ExperimentalTime

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
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(notifications) { it -> // loops over each notification in the list
                            NotificationItem(
                                event = it.event,
                                read = it.read,
                                // set this notification to unread when clicked
                                onClick = {
                                    Log.d("NOTIFY", "Clicked notification: ${it.notificationId}")
                                    selectedNotification.value =
                                        it // store the clicked notifications
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedNotification.value?.let { notification ->
        // TODO: instead, redirect to the correct screen
//        AlertDialog(
//            onDismissRequest = {
//                notificationViewModel.markNotificationsAsRead(notification.notificationId)
//                selectedNotification.value = null
//            },
//            title = {
//                Text("Notification")
//            },
//            text = {
//                Text(notification.content)
//            },
//            confirmButton = {
//                Text(
//                    text = "Close",
//                    modifier = Modifier.clickable {
//                        notificationViewModel.markNotificationsAsRead(notification.notificationId)
//                        selectedNotification.value = null
//                    }
//                )
//            }
//        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun NotificationItem(
    event: BaseEvent,
    read: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (read) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.2F
                )
            )
            .clickable { onClick() }
    ) {
        // todo() hier een when op basis van event type een extra compasable voor highscore, friendrequest enzo daar een template voor maken
        Icon(
            imageVector = when(event) {
                is FriendRequestEvent -> {
                    Icons.Filled.Person
                }
                else -> Icons.Filled.EmojiEvents
            },
            contentDescription = null,
            modifier = Modifier.padding(start = 12.dp)
        )
        Text(
            text = "${event.created} ${if (read) "(Read)" else "(Unread)" }", modifier = Modifier.padding(16.dp)
        )
    }
}