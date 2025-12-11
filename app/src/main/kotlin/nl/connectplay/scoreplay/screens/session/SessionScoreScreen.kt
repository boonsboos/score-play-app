package nl.connectplay.scoreplay.screens.session

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.room.events.SessionEvent

@Composable
fun SessionScoreScreen(
    backStack: NavBackStack<NavKey>,
    onEvent: (SessionEvent) -> Unit,
) { }