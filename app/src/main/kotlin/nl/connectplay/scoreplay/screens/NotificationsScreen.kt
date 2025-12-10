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

@Composable
fun NotificationsScreen(
    backStack: NavBackStack<NavKey>,
    notificationViewModel: NotificationListViewModel = koinViewModel()
) {
    val notifications by notificationViewModel.state.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Notifications", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
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
                    LazyColumn {
                        items(notifications) { it -> // loops over each notification in the list
                            NotificationItem(
                                content = it.content + if (it.read) " (Read)" else " (Unread)",
                                read = it.read,
                                // set this notification to unread when clicked
                                onClick = {
                                    Log.d("NOTIFY", "Clicked notification: ${it.notificationId}")
                                    notificationViewModel.markNotificationsAsRead(
                                        it
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    content: String, read: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit
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

        Text(
            text = content, modifier = Modifier.padding(16.dp)
        )
    }
}