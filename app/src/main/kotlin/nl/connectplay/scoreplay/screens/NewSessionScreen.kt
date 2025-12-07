package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.game.Game
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.Stepper
import nl.connectplay.scoreplay.viewModels.GamesListViewModel
import org.koin.androidx.compose.koinViewModel
import nl.connectplay.scoreplay.models.SessionVisibility

@Composable
fun NewSessionScreen(backStack: NavBackStack<NavKey>) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    var selectedVisibility by remember { mutableStateOf(SessionVisibility.ANONYMISED) }

    val stepTitles = listOf("Game", "Visibility", "Players")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "New Session") },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Stepper(
                steps = stepTitles,
                currentStep = currentStep,
                onStepChange = { step ->
                    if (step > currentStep) {
                        if (currentStep == 0 && selectedGame == null) return@Stepper
                    }
                    currentStep = step
                },
                canGoNext = when (currentStep) {
                    0 -> selectedGame != null
                    else -> true
                }
            ) { step ->
                when (step) {
                    0 -> ChooseGameStep(
                        selectedGame = selectedGame,
                        onGameSelected = { selectedGame = it }
                    )
                    1 -> ChooseVisibilityStep(
                        selectedVisibility = selectedVisibility,
                        onVisibilitySelected = { selectedVisibility = it }
                    )
                    2 -> PlayerStep()
                }
            }
        }
    }
}

@Composable
fun ChooseGameStep(
    selectedGame: Game?,
    onGameSelected: (Game?) -> Unit,
    viewModel: GamesListViewModel = koinViewModel()
) {
    val games by viewModel.gamesList.collectAsState()
    val loading by viewModel.areLoading.collectAsState()

    LaunchedEffect(Unit) { if (games.isEmpty() && !loading) { viewModel.fetch() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Choose a Game to play",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> Text("Loading games…")
            games.isEmpty() -> Text("No games found")
            else -> {
                LazyColumn {
                    items(games, key = { it.id }) { game ->
                        val isSelected = selectedGame?.id == game.id
                        val bgColor =
                            if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface

                        ListItem(
                            modifier = Modifier
                                .background(bgColor)
                                .clickable { onGameSelected(if (isSelected) null else game)
                                }
                                .padding(vertical = 2.dp),
                            headlineContent = { Text(game.name) },
                            overlineContent = { Text(game.publisher) },
                            supportingContent = {
                                Text(
                                    game.description,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            trailingContent = {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        onGameSelected(if (checked) game else null)
                                    }
                                )
                            },
                            leadingContent = {
                                Icon(Icons.Default.Image, contentDescription = null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChooseVisibilityStep(
    selectedVisibility: SessionVisibility,
    onVisibilitySelected: (SessionVisibility) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Who can see this session?",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        VisibilityOptionRow(
            value = SessionVisibility.PRIVATE,
            title = "Private",
            description = "Only you can see this session.",
            selected = selectedVisibility,
            onSelected = onVisibilitySelected
        )

        VisibilityOptionRow(
            value = SessionVisibility.FRIENDS_ONLY,
            title = "Friends only",
            description = "Visible to your friends.",
            selected = selectedVisibility,
            onSelected = onVisibilitySelected
        )

        VisibilityOptionRow(
            value = SessionVisibility.ANONYMISED,
            title = "Anonymised",
            description = "Scores are public, your name is hidden.",
            selected = selectedVisibility,
            onSelected = onVisibilitySelected
        )

        VisibilityOptionRow(
            value = SessionVisibility.PUBLIC,
            title = "Public",
            description = "Everyone can see this session and your name.",
            selected = selectedVisibility,
            onSelected = onVisibilitySelected
        )
    }
}

@Composable
private fun VisibilityOptionRow(
    value: SessionVisibility,
    title: String,
    description: String,
    selected: SessionVisibility,
    onSelected: (SessionVisibility) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(value) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected == value,
            onClick = { onSelected(value) }
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun PlayerStep() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Add players to the Game session",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Friend selector here..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Add custom player here..."
        )
    }
}
