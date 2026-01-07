package nl.connectplay.scoreplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import nl.connectplay.scoreplay.ui.components.Navigator
import nl.connectplay.scoreplay.ui.notifications.NotificationBuilder
import nl.connectplay.scoreplay.ui.notifications.NotificationChannelProvider
import nl.connectplay.scoreplay.ui.theme.ScorePlayTheme
import nl.connectplay.scoreplay.ui.notifications.NotificationPermission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationChannelProvider.create(this)

        setContent {
            ScorePlayTheme {
                NotificationPermission() // request the notifications permission when the app starts
                Navigator(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}