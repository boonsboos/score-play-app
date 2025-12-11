package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterButton(title: String, selected: Boolean, onClick: () -> Unit) {
    // change button color when selected
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            if (selected) MaterialTheme.colorScheme.primary else
                MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.height(38.dp)
    ) {
        Text(title)
    }
}