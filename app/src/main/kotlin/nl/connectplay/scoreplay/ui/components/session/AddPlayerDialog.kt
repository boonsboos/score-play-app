package nl.connectplay.scoreplay.ui.components.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.connectplay.scoreplay.models.friends.UserFriend

@Composable
fun AddPlayerDialog(
    friends: List<UserFriend>,
    onDismiss: () -> Unit,
    onAddFriend: (userId: Int, guestName: String) -> Unit,
    onAddGuest: (guestName: String) -> Unit
) {
    var isFriendMode by remember { mutableStateOf(true) }
    var newPlayerName by remember { mutableStateOf("") }
    var selectedFriendId by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add player") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = {
                            isFriendMode = true
                            newPlayerName = ""
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isFriendMode) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Text("Friend") }

                    TextButton(
                        onClick = {
                            isFriendMode = false
                            selectedFriendId = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (!isFriendMode) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    ) { Text("Guest") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isFriendMode) {
                    friends.forEach { friend ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedFriendId == friend.user.id,
                                onCheckedChange = { checked ->
                                    selectedFriendId = if (checked) friend.user.id else null
                                }
                            )
                            Text(friend.user.username)
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        label = { Text("Player name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = if (isFriendMode) selectedFriendId != null else newPlayerName.isNotBlank(),
                onClick = {
                    if (isFriendMode) {
                        val friend = friends.first { it.user.id == selectedFriendId }
                        onAddFriend(friend.user.id, friend.user.username)
                    } else {
                        onAddGuest(newPlayerName.trim())
                    }

                    // local reset (dialog closes anyway)
                    newPlayerName = ""
                    selectedFriendId = null
                    onDismiss()
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}