package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.Stepper

@Composable
fun NewSessionScreen(backStack: NavBackStack<NavKey>) {
    var currentStep by remember { mutableStateOf(0) }
    val stepTitles = listOf("Setup", "Players", "Review")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session") },
        bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            @Composable
            fun SetupStep() {
                Text("Setup content")
            }

            @Composable
            fun PlayerStep() {
                Text("Player selection")
            }

            @Composable
            fun ReviewStep() {
                Text("Review everything")
            }

            Stepper(
                steps = stepTitles,
                currentStep = currentStep,
                onStepChange = { currentStep = it }
            ) { step ->
                when (step) {
                    0 -> SetupStep()
                    1 -> PlayerStep()
                    2 -> ReviewStep()
                }
            }
        }
    }
}