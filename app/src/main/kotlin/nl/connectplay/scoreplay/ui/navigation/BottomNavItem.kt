package nl.connectplay.scoreplay.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Notifications

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(
        route = "home",
        icon = Icons.Filled.Home,
        label = "Home"
    )
    object Friends : BottomNavItem(
        route = "friends",
        icon = Icons.Filled.People,
        label = "Friends"
    )
    object Games : BottomNavItem(
        route = "games",
        icon = Icons.Filled.SportsEsports,
        label = "Games"
    )
    object Notifications : BottomNavItem(
        route = "notifications",
        icon = Icons.Filled.Notifications,
        label = "Notifications"
    )
}