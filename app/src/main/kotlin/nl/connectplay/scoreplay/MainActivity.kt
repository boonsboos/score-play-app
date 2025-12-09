package nl.connectplay.scoreplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import nl.connectplay.scoreplay.ui.components.Navigator
import nl.connectplay.scoreplay.ui.theme.ScorePlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScorePlayTheme {
                Navigator(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}