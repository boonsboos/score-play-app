package nl.connectplay.scoreplay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        // Using the custom ScorePlayButton component
                        // we show a toast message when the button is clicked
                        ScorePlayButton(label = "Hello World", onClick = {
                            Toast.makeText(this@MainActivity, "Button clicked!", Toast.LENGTH_SHORT)
                                .show()
                        })
                    }
                }
            }
        }
    }
}
