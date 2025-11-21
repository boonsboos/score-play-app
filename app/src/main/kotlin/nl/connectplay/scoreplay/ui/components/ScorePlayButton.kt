package nl.connectplay.scoreplay.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScorePlayButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            // set the color to our defined primary color
            contentColor = MaterialTheme.colorScheme.onPrimary, // is for the text/icon color
            containerColor = MaterialTheme.colorScheme.primary // is for the button background color
        )
    ) {
        // Button contents, here just a text label
        Text(text = label)
    }
}