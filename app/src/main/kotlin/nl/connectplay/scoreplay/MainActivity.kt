package nl.connectplay.scoreplay

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import nl.connectplay.scoreplay.ui.components.Navigator
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.theme.ScorePlayTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScorePlayTheme {
                /* A Scaffold is a layout structure that provides slots
                 * for the most common top-level material components:
                 * TopBar, BottomBar and FloatingActionButton.
                 * 
                 * But add those top-level components to the screen itself,
                 * not here because not all screens need such component
                 * think about sign-in that does not need an bottombar
                 */
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigator(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
