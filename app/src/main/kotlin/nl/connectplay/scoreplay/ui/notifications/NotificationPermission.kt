package nl.connectplay.scoreplay.ui.notifications

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermission() {
    val notificationPermissionState =
        // remember the notification permission: granted, denied or not yet asked
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    // this launchedEffect will be executed only once
    LaunchedEffect(Unit) {
        if (!notificationPermissionState.status.isGranted) {
            notificationPermissionState.launchPermissionRequest() // show android permission dialog
        }
    }
}