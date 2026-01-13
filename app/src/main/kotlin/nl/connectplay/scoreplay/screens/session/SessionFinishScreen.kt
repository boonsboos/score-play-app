package nl.connectplay.scoreplay.screens.session

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.models.SessionVisibility
import nl.connectplay.scoreplay.room.events.SessionEvent
import nl.connectplay.scoreplay.screens.Screens
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.PhotoPickerSheet
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.session.RoundScoreRow
import nl.connectplay.scoreplay.viewModels.session.SessionEndImageState
import nl.connectplay.scoreplay.viewModels.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun SessionFinishScreen(
    backStack: NavBackStack<NavKey>,
) {
    // Use the Activity as the ViewModelStoreOwner so this VM instance is shared across session screens.
    val activity = LocalContext.current as? ComponentActivity ?: return
    val sessionViewModel: SessionViewModel = koinViewModel(viewModelStoreOwner = activity)

    val state by sessionViewModel.state.collectAsState()
    val onEvent = sessionViewModel::onEvent

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    var showImagePicker by remember { mutableStateOf(false) }
    val imageUri by sessionViewModel.sessionEndImage.collectAsState()

    // Load the persisted session snapshot once when the screen enters, including winner calculation.
    LaunchedEffect(Unit) {
        sessionViewModel.loadActiveSessionFromDb(computeWinner = true)
    }

    // Collect one-off snackbar events
    LaunchedEffect(Unit) {
        sessionViewModel.snackbar.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val winnerName = state.winnerPlayer?.let { it.guestName ?: stringResource(R.string.you) }
        ?: stringResource(R.string.you)
    val winnerScore = state.winnerScore ?: 0.0

    var selectedVisibility by remember(state.visibility) { mutableStateOf(state.visibility) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ScorePlayTopBar(
                title = stringResource(R.string.screen_session_title),
                backStack = backStack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onEvent(SessionEvent.FinishSession(onFinished = {
                        backStack.add(Screens.Leaderboard(state.gameId ?: 1))
                    }))
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.session_finish_cta_upload)
                )
                Text(text = stringResource(R.string.session_finish_cta_upload))
            }
        },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.session_finish_winnder, winnerName),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            RoundScoreRow(
                name = winnerName,
                score = winnerScore
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.session_finish_visibility_label),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(8.dp))

                // Visibility selection updates both local UI state and persists to Room via the event.
                SingleChoiceSegmentedButtonRow(modifier = Modifier) {
                    SessionVisibility.entries.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = selectedVisibility == option,
                            onClick = {
                                selectedVisibility = option
                                onEvent(SessionEvent.UpdateVisibility(option))
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = SessionVisibility.entries.size
                            )
                        ) {
                            Text(
                                text = option.toLabel(),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.session_finish_picture_label),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(
                        onClick = { showImagePicker = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Upload")
                    }
                }

                if (showImagePicker) {
                    PhotoPickerSheet(
                        prompt = stringResource(R.string.session_finish_cta_picture),
                        onDismissRequest = { showImagePicker = false },
                        onPictureTaken = { imageUri ->
                            sessionViewModel.addImage(SessionEndImageState(imageUri, context))
                        }
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.session_finish_picture_description),
                    style = MaterialTheme.typography.labelMedium
                )

                imageUri?.let { imageState ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        FallbackImage(
                            url = imageState.image,
                            size = 300.dp,
                        ) { /* no fallback required as URL is always non-null */ }
                    }
                }
            }
        }
    }
}