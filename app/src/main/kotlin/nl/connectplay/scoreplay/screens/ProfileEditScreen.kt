package nl.connectplay.scoreplay.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayInputField
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.profile.ProfileEditViewModel
import nl.connectplay.scoreplay.viewModels.profile.UiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    backStack: NavBackStack<NavKey>,
    currentUser: UserProfile,
    modifier: Modifier = Modifier,
    profileEditViewModel: ProfileEditViewModel = koinViewModel(
        parameters = { parametersOf(currentUser) }
    ),
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    val profileState by profileEditViewModel.updatedProfileState.collectAsState()
    val usernameState by profileEditViewModel.username.collectAsState()
    val emailState by profileEditViewModel.email.collectAsState()
    val pendingImage by profileEditViewModel.pendingImageUri.collectAsState()
    val pictureUrl by profileEditViewModel.pictureUrl.collectAsState()
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    fun createCameraUri() {
        val file = createImageFile(context)
        cameraUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            imageUri = cameraUri
            profileEditViewModel.onPictureChanged(cameraUri!!)
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            profileEditViewModel.onPictureChanged(it)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            createCameraUri()
            takePhotoLauncher.launch(cameraUri!!)
        } else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success) backStack.removeAt(backStack.lastIndex)
    }

    Scaffold(
        topBar = {
            ScorePlayTopBar(
                title = "Edit profile",
                backStack = backStack
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {

            Spacer(Modifier.height(24.dp))

            // =============================
            // Profile image card
            // =============================
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FallbackImage(
                        url = pendingImage ?: pictureUrl,
                        size = 140.dp,
                        shape = CircleShape,
                        modifier = Modifier.clickable { showPicker = true },
                    ) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Profile picture",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScorePlayInputField(
                    value = usernameState,
                    placeholder = "Username",
                    onChange = profileEditViewModel::onUsernameChanged,
                    enabled = profileState !is UiState.Loading
                )

                ScorePlayInputField(
                    value = emailState,
                    placeholder = "Email address",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    onChange = profileEditViewModel::onEmailChanged,
                    enabled = profileState !is UiState.Loading
                )
            }

            if (profileState is UiState.Error) {
                Text(
                    text = (profileState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            ScorePlayButton(
                label = if (profileState is UiState.Loading)
                    "Saving..."
                else
                    "Save changes",
                enabled = profileState !is UiState.Loading,
                onClick = { profileEditViewModel.onSaveProfile(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
        if (showPicker) {
            ModalBottomSheet(
                onDismissRequest = { showPicker = false }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Change profile picture",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row() {
                        ScorePlayButton(
                            label = "Take photo",
                            onClick = {
                                showPicker = false
                                when (
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    )
                                ) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        createCameraUri()
                                        takePhotoLauncher.launch(cameraUri!!)
                                    }

                                    else ->
                                        requestPermissionLauncher.launch(
                                            Manifest.permission.CAMERA
                                        )
                                }
                            }
                        )

                        ScorePlayButton(
                            label = "Choose from gallery",
                            onClick = {
                                showPicker = false
                                pickImageLauncher.launch("image/*")
                            }
                        )
                    }
                }
            }
        }
    }
}

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}
