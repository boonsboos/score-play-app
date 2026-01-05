package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.viewModels.NotificationBadgeViewModel

@Composable
fun BottomNavBar(backStack: NavBackStack<NavKey>, badgeViewModel: NotificationBadgeViewModel) {
    // with the lastOrnull we get the lates screen that were on
    val currentScreen = backStack.lastOrNull()
    val hasUnreadNotifications by badgeViewModel.hasUnreadNotifications.collectAsState()

    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screens.Home,
            onClick = { backStack.add(Screens.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                if (currentScreen == Screens.Home) {
                    Text("Home")
                }
            }
        )
        NavigationBarItem(
            selected = currentScreen == Screens.Friends,
            onClick = { backStack.add(Screens.Friends) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = "Friends"
                )
            },
            label = {
                if (currentScreen == Screens.Friends) {
                    Text("Friends")
                }
            }
        )
        NavigationBarItem(
            selected = currentScreen == Screens.Games,
            onClick = { backStack.add(Screens.Games) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = "Games"
                )
            },
            label = {
                if (currentScreen == Screens.Games) {
                    Text("Games")
                }
            }
        )
        NavigationBarItem(
            selected = currentScreen == Screens.Notifications,
            onClick = { backStack.add(Screens.Notifications) },
            icon = {
                Box {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications"
                    )

                    // badge will be removed when client is on notificationscreen
                    if (hasUnreadNotifications && currentScreen != Screens.Notifications) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            },
            label = {
                if (currentScreen == Screens.Notifications) {
                    Text("Notifications")
                }
            }
        )
    }
}