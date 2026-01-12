package nl.connectplay.scoreplay.ui.components.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun RoundScoreRow(
    name: String,
    score: Double,
    modifier: Modifier = Modifier
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        headlineContent = {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            Text(
                text = formatScore(score),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

private fun formatScore(score: Double): String {
    // If you want integers when possible:
    val asInt = score.roundToInt()
    return if (kotlin.math.abs(score - asInt) < 0.000001) asInt.toString() else score.toString()
}
