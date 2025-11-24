package nl.connectplay.scoreplay.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntry?.destination

    NavigationBar {
        NavigationBarItem(
            selected = false, // with false means this items is not active
            onClick = { /* TODO() handle navigation */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }
        )
        NavigationBarItem(
            selected = false, // with false means this items is not active
            onClick = { /* TODO() handle navigation */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.People,
                    contentDescription = "Friends"
                )
            }
        )
        NavigationBarItem(
            selected = false, // with false means this items is not active
            onClick = { /* TODO() handle navigation */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.SportsEsports,
                    contentDescription = "Games"
                )
            }
        )
        NavigationBarItem(
            selected = false, // with false means this items is not active
            onClick = { /* TODO() handle navigation */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications"
                )
            }
        )
    }
}