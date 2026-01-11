package nl.connectplay.scoreplay.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.connectplay.scoreplay.models.game.FollowedGame


@Composable
fun FollowedGameItem(
    game: FollowedGame,
    modifier: Modifier = Modifier,
    onUserClick: (playerId: Int) -> Unit = { }
) {
    var expanded by rememberSaveable(game.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = game.name,
            style = MaterialTheme.typography.titleMedium
        )

        if (expanded) {
            Spacer(Modifier.height(8.dp))

            game.podium.forEach { podiumItem ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    FallbackImage(url = null, size = 24.dp) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = podiumItem.playerName.first().toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Column {
                        Text(
                            text = podiumItem.playerName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Score: " + podiumItem.score.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                        contentDescription = "View details",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
//                            .clickable { onUserClick(podiumItem.playerId) }
                    )
                }
            }
        }
    }
}