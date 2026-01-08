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
import nl.connectplay.scoreplay.models.session.RoundScoreInput
import nl.connectplay.scoreplay.room.entities.RoomSessionPlayer

@Composable
fun AddRoundDialog(
    players: List<RoomSessionPlayer>,
    onDismiss: () -> Unit,
    onSave: (List<RoundScoreInput>) -> Unit
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
                    val label = player.guestName ?: "You"

                    OutlinedTextField(
                        value = scores[player.sessionPlayerId] ?: "",
                        onValueChange = {
                            scores[player.sessionPlayerId] = it
                        },
                        label = { Text(label) },
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
                    val inputs = players.mapNotNull { player ->
                        val value = scores[player.sessionPlayerId]
                            ?.trim()
                            ?.toDoubleOrNull()
                            ?: return@mapNotNull null

                        RoundScoreInput(
                            sessionPlayerId = player.sessionPlayerId,
                            score = value
                        )
                    }

                    onSave(inputs)
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
