package nl.connectplay.scoreplay.ui.components.session

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import nl.connectplay.scoreplay.R

@Composable
fun FinishSessionDialog(
    completedRoundCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.finish_session_dialog_title)) },
        text = {
            Text(
                text = stringResource(R.string.finish_session_dialog_description, completedRoundCount),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(R.string.finish_session_dialog_finish))
            }
        }
    )
}