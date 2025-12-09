package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowRightAlt
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import nl.connectplay.scoreplay.exceptions.InvalidTokenException
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayButton
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.viewModels.profile.ProfileViewModel
import nl.connectplay.scoreplay.viewModels.profile.UiState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun ProfileScreen(
    backStack: NavBackStack<NavKey>,
    targetUserId: Int?,
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(parameters = { parametersOf(targetUserId) }),
    tokenStore: TokenDataStore = koinInject()
) {
    val userId by tokenStore.userId.collectAsState(null) // currently logged in user id
    val profileState by profileViewModel.profileState.collectAsState()
    val sessionsState by profileViewModel.sessionsState.collectAsState()
    val gamesState by profileViewModel.gamesState.collectAsState()

    val title = when (val state = profileState) {
        is UiState.Success -> state.data.username
        UiState.Loading -> "Loading…"
        is UiState.Error -> "Error"
    }

    LaunchedEffect(profileState) {
        // check if error is TokenInvalid and handle logout
        if (profileState is UiState.Error) {
            val exception = (profileState as UiState.Error).exception
            if (exception is InvalidTokenException) {
                backStack.apply {
                    while (isNotEmpty()) removeLast()
                    add(Screens.Login)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title, backStack) },
        bottomBar = { BottomNavBar(backStack) },
        floatingActionButton = {
            if (profileState is UiState.Success) {
                val profile = (profileState as UiState.Success).data
                if (profile.id == userId)
                    FloatingActionButton(onClick = {
                        TODO("Add function to edit user")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Profile Picture"
                        )
                    }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (profileState) {
                UiState.Loading -> LoadingSection()
                is UiState.Error -> ErrorSection((profileState as UiState.Error))
                is UiState.Success -> {
                    val profile = (profileState as UiState.Success).data

                    Spacer(modifier = Modifier.size(20.dp))
                    ProfileAvatar(url = profile.picture)

                    Spacer(modifier = Modifier.size(24.dp))
                    if (profile.id != userId) {
                        ScorePlayButton(
                            label = "Request Friend",
                            onClick = { /* TODO: Implement friend request */ })
                    }

                    StateSection(sessionsState) { state ->
                        val items = state.data
                        if (items.isEmpty()) {
                            SectionHeader("Last sessions", empty = true)
                            Text(
                                text = "No items found",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            SectionHeader(
                                "Last ${state.data.size} sessions",
                                onClick = { /* TODO: Navigate to all sessions */ })
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxWidth()
                            ) {
                                items(items) { session ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(75.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                            .padding(horizontal = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        FallbackImage(
                                            url = session.endOfSessionPictureUrl,
                                            size = 75.dp
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(75.dp),
                                                imageVector = Icons.Outlined.Image,
                                                contentDescription = ""
                                            )
                                        }
                                        Column(
                                            modifier = Modifier.height(75.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.Start,
                                        ) {
                                            Text(
                                                text = session.game.name,
                                                modifier = Modifier,
                                            )
                                            Text(
                                                text = session.startTime.format(
                                                    LocalDateTime.Format {
                                                        day()
                                                        char('-')
                                                        monthNumber()
                                                        char('-')
                                                        year()
                                                        char(' ')
                                                        hour()
                                                        char(':')
                                                        minute()
                                                    }),
                                                modifier = Modifier
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(24.dp))

                    StateSection(gamesState) { state ->
                        val items = state.data
                        if (items.isEmpty()) {
                            SectionHeader("Followed games", empty = true)
                            Text(
                                text = "No items found",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            SectionHeader(
                                "Followed Games (${state.data.size})",
                                onClick = { /* TODO: Navigate to all games */ })
                            LazyColumn(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                items(items) { game ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(75.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1F))
                                            .padding(horizontal = 20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        FallbackImage(
                                            url = null, // should be game.picture when available
                                            size = 75.dp
                                        ) {
                                            Icon(
                                                modifier = Modifier.size(75.dp),
                                                imageVector = Icons.Outlined.Image,
                                                contentDescription = ""
                                            )
                                        }
                                        Text(
                                            text = game.name,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAvatar(
    url: String?,
) {
    FallbackImage(
        url = url,
        size = 128.dp,
        shape = CircleShape,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size((128.dp) * 0.75f)
        )
    }
}


@Composable
fun LoadingSection() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading…")
    }
}

@Composable
fun ErrorSection(errorState: UiState.Error) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorState.message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    empty: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Start
        )

        if (!empty) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowRightAlt,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "View all",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(onClick = onClick),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun <T> StateSection(
    uiState: UiState<T>,
    content: @Composable (UiState.Success<T>) -> Unit
) {
    when (uiState) {
        UiState.Loading -> LoadingSection()
        is UiState.Error -> ErrorSection(uiState)
        is UiState.Success -> content(uiState)
    }
}