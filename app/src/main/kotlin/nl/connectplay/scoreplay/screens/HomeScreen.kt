package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel = koinViewModel()
) {
    // the scaffold makes the basic bottomnav layout
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Home", backStack = backStack) },
        // bottombar uses the backStack on witch screen we are
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // to logout the app for test!
            ScorePlayButton(
                label = "TEST Logout",
                onClick = { onLogout() }
            )

            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    sessionViewModel.onEvent(SessionEvent.StartNewSession)
                    backStack.apply { add(Screens.SessionSetup) }
                }
            ) {
                Text("New Session")
            }
        }
    }
}
