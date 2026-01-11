package nl.connectplay.scoreplay.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerSheet(
    prompt: String,
    onDismissRequest: () -> Unit,
    onPictureTaken: (photo: Uri) -> Unit
) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }

    fun createCameraUri() {
        val file = createImageFile(context)
        cameraUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()

    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onPictureTaken(cameraUri)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onPictureTaken(uri)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            createCameraUri()
            takePhotoLauncher.launch(cameraUri)
        } else Toast.makeText(context, R.string.photo_picker_permission_denied, Toast.LENGTH_SHORT).show()
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = prompt,
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ScorePlayButton(
                    label = stringResource(R.string.photo_picker_camera),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            when (
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                )
                            ) {
                                PackageManager.PERMISSION_GRANTED -> {
                                    createCameraUri()
                                    takePhotoLauncher.launch(cameraUri)
                                }

                                else -> {
                                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        }
                    }
                )

                ScorePlayButton(
                    label = stringResource(R.string.photo_picker_gallery),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            pickImageLauncher.launch("image/*")
                        }
                    }
                )
            }
        }
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}