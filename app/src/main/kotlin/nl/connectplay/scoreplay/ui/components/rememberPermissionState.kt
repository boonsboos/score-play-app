package nl.connectplay.scoreplay.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


@Composable
fun rememberPermissionState(
    permission: String,
    onResult: (Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> onResult(granted) }
    )

    // Return a lambda to request the permission
    return { launcher.launch(permission) }
}