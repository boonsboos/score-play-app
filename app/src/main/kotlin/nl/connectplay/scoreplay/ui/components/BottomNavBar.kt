package nl.connectplay.scoreplay.ui.components

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.viewModels.NotificationBadgeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BottomNavBar(backStack: NavBackStack<NavKey>) {
    // with the lastOrnull we get the lates screen that were on
    val currentScreen = backStack.lastOrNull()
    val badgeViewModel: NotificationBadgeViewModel = koinViewModel()
    val hasUnreadNotifications by badgeViewModel.hasUnreadNotifications.collectAsStateWithLifecycle()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = currentScreen == Screens.Home,
            onClick = { backStack.add(Screens.Home) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = stringResource(R.string.screen_home_title)
                )
            },
            label = {
                if (currentScreen == Screens.Home) {
                    Text(text = stringResource(R.string.screen_home_title))
                }
            }
        )
        NavigationBarItem(
            selected = currentScreen == Screens.Friends,
            onClick = { backStack.add(Screens.Friends) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = stringResource(R.string.screen_friends_title)
                )
            },
            label = {
                if (currentScreen == Screens.Friends) {
                    Text(text = stringResource(R.string.screen_friends_title))
                }
            }
        )
        NavigationBarItem(
            selected = currentScreen == Screens.Games,
            onClick = { backStack.add(Screens.Games) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = stringResource(R.string.screen_games_title)
                )
            },
            label = {
                if (currentScreen == Screens.Games) {
                    Text(text = stringResource(R.string.screen_games_title))
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
                        contentDescription = stringResource(R.string.screen_notifications_title)
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
                    Text(text = stringResource(R.string.screen_notifications_title))
                }
            }
        )
    }
}