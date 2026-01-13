package nl.connectplay.scoreplay.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.models.user.UserProfile
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.PhotoPickerSheet
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayInputField
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.UiState
import nl.connectplay.scoreplay.viewModels.profile.ProfileEditViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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

    val profileState by profileEditViewModel.updatedProfileState.collectAsState()
    val usernameState by profileEditViewModel.username.collectAsState()
    val emailState by profileEditViewModel.email.collectAsState()
    val pendingImage by profileEditViewModel.pendingImageUri.collectAsState()
    val pictureUrl by profileEditViewModel.pictureUrl.collectAsState()

    var showPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profileState) {
        if (profileState is UiState.Success) backStack.removeAt(backStack.lastIndex)
        // check if error is TokenInvalid and handle logout
        if (profileState is UiState.Error) {
            val exception = (profileState as UiState.Error).exception
            if (exception is InvalidTokenException) {
                backStack.apply {
                    while (isNotEmpty()) removeAt(lastIndex)
                    add(Screens.Login)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ScorePlayTopBar(
                title = stringResource(R.string.screen_edit_profile_title),
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
                FallbackImage(
                    url = pendingImage ?: pictureUrl,
                    size = 144.dp,
                    shape = CircleShape,
                    modifier = Modifier
                        .clickable { showPicker = true }
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Icon(
                        modifier = Modifier.size((144.dp) * 0.60f),
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(R.string.profile_picture),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScorePlayInputField(
                    value = usernameState,
                    placeholder = stringResource(R.string.text_field_username),
                    onChange = profileEditViewModel::onUsernameChanged,
                    enabled = profileState !is UiState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )

                ScorePlayInputField(
                    // should never be null, but UserProfile.email is nullable for other parts of the program.
                    value = emailState!!,
                    placeholder = stringResource(R.string.text_field_email),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    onChange = profileEditViewModel::onEmailChanged,
                    enabled = profileState !is UiState.Loading,
                    modifier = Modifier.fillMaxWidth()
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

            Column(
                modifier = Modifier
                    .padding(bottom = 44.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScorePlayButton(
                    label = if (profileState is UiState.Loading)
                        stringResource(R.string.profile_edit_saving)
                    else
                        stringResource(R.string.profile_edit_cta),
                    enabled = profileState !is UiState.Loading,
                    onClick = {
                        profileEditViewModel.onSaveProfile(context)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(bottom = 12.dp)
                )

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    onClick = { showDeleteDialog = true }
                ) {
                    Text(stringResource(R.string.profile_edit_delete_account))
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.profile_edit_delete_dialog_title)) },
                    text = { Text(stringResource(R.string.profile_edit_delete_dialog_text)) },
                    confirmButton = {
                        Button(onClick = {
                            showDeleteDialog = false
                            profileEditViewModel.deleteAccount()
                        }) { Text(stringResource(R.string.profile_edit_delete_dialog_confirm)) }
                    },
                    dismissButton = {
                        Button(onClick = {
                            showDeleteDialog = false
                        }) { Text(stringResource(R.string.profile_edit_delete_dialog_deny)) }
                    }
                )
            }
        }
        if (showPicker) {
            PhotoPickerSheet(
                prompt = stringResource(R.string.profile_edit_cta_profile_picture),
                onDismissRequest = { showPicker = false },
                onPictureTaken = {
                    profileEditViewModel.onPictureChanged(it)
                }
            )
        }
    }
}
