package nl.connectplay.scoreplay.ui.components.session

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.screens.Screens

@Composable
fun SessionTabs(
    backStack: NavBackStack<NavKey>,
    currentScreen: NavKey,
    canProgress: Boolean = true
) {
    TabRow(
        selectedTabIndex = when (currentScreen) {
            Screens.SessionSetup -> 0
            Screens.SessionScore -> 1
            else -> 0
        }
    ) {
        Tab(
            selected = currentScreen == Screens.SessionSetup,
            onClick = {
                if (currentScreen != Screens.SessionSetup) {
                    backStack.add(Screens.SessionSetup)
                }
            },
            text = { Text(text = stringResource(R.string.session_tab_setup)) }
        )

        Tab(
            enabled = canProgress,
            selected = currentScreen == Screens.SessionScore,
            onClick = {
                if (currentScreen != Screens.SessionScore) {
                    backStack.add(Screens.SessionScore)
                }
            },
            text = {
                Text(
                    text = stringResource(R.string.session_tab_scores),
                    color = if (!canProgress) {
                        MaterialTheme.colorScheme.surfaceDim
                    } else {
                        Color.Unspecified
                    }
                )
            }
        )
    }
}
