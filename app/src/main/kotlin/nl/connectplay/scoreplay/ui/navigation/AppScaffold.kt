package nl.connectplay.scoreplay.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost

@Composable
fun AppScaffold() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
        }
    }
}