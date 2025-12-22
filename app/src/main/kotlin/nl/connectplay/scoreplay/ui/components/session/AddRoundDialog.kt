package nl.connectplay.scoreplay.ui.components.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer
import nl.connectplay.scoreplay.room.entities.RoomSessionScore

@Composable
fun AddRoundDialog(
    sessionId: Int,
    gameId: Int,
    players: List<RoomSessionPlayer>,
    onDismiss: () -> Unit,
    onSave: (List<RoomSessionScore>) -> Unit
) {
    val scores = remember {
        mutableStateMapOf<Int, String>().apply {
            players.forEach { put(it.sessionPlayerId, "") }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New round") },
        text = {
            Column {
                players.forEach { player ->
                    OutlinedTextField(
                        value = scores[player.sessionPlayerId] ?: "",
                        onValueChange = {
                            scores[player.sessionPlayerId] = it
                        },
                        label = { Text(player.guestName.toString()) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val roundScores = players.mapNotNull { player ->
                        val value = scores[player.sessionPlayerId]
                            ?.toDoubleOrNull()
                            ?: return@mapNotNull null

                        RoomSessionScore(
                            sessionId = sessionId,
                            gameId = gameId,
                            sessionPlayerId = player.sessionPlayerId,
                            score = value
                        )
                    }
                    onSave(roundScores)
                }
            ) {
                Text("Save round")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
