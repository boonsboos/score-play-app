package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import nl.connectplay.scoreplay.ui.components.BottomNavBar

@Composable
fun HomeScreen(backStack: NavBackStack) {
    // the scaffold makes the basic bottomnav layout
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // bottombar uses the backStack on witch screen we are
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}
