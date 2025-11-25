package nl.connectplay.scoreplay.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import nl.connectplay.scoreplay.screens.Screens

@Composable
fun BottomNavBar(backStack: NavBackStack) {
    val currentScreen = backStack.lastOrNull()

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
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications"
                )
            },
            label = {
                if (currentScreen == Screens.Notifications) {
                    Text("Notifications")
                }
            }
        )
    }
}