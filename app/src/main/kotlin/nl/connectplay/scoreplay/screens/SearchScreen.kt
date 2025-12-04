package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.search.SearchFilter
import nl.connectplay.scoreplay.models.search.SearchResult
import nl.connectplay.scoreplay.viewModels.SearchViewModel

@Composable
fun SearchScreen(
    backStack: NavBackStack<NavKey>, // nu nog niet gebruikt, maar kan later voor navigatie
    modifier: Modifier = Modifier,
    initialQuery: String? = null,
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit = {},
    onGameClick: (String) -> Unit = {}
) {
    // propagate query from Search event to ViewModel
    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            viewModel.setQuery(initialQuery)
            viewModel.search()
        }
    }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val results by viewModel.results.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ------------------ SEARCH FIELD ------------------
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.setQuery(it)
                viewModel.search()
            },
            placeholder = { Text("Search users or games…") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ------------------ FILTER BUTTONS ------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterButton(
                title = "All",
                selected = filter == SearchFilter.ALL,
                onClick = {
                    viewModel.setFilter(SearchFilter.ALL)
                    viewModel.search()
                }
            )
            FilterButton(
                title = "Users",
                selected = filter == SearchFilter.USERS,
                onClick = {
                    viewModel.setFilter(SearchFilter.USERS)
                    viewModel.search()
                }
            )
            FilterButton(
                title = "Games",
                selected = filter == SearchFilter.GAMES,
                onClick = {
                    viewModel.setFilter(SearchFilter.GAMES)
                    viewModel.search()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ------------------ RESULTS LIST ------------------
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(results) { item ->
                when (item) {
                    is SearchResult.UserResult -> {
                        UserResultRow(
                            username = item.username,
                            pictureUrl = item.pictureUrl,
                            onClick = { onUserClick(item.userId) }
                        )
                    }

                    is SearchResult.GameResult -> {
                        GameResultRow(
                            title = item.title,
                            description = item.description,
                            onClick = { onGameClick(item.gameId) }
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------ //
// ---------------------- SUB COMPONENTS ----------------------- //
// ------------------------------------------------------------ //

@Composable
fun FilterButton(title: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = if (selected)
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        else
            ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
        modifier = Modifier.height(38.dp)
    ) {
        Text(title)
    }
}

@Composable
fun UserResultRow(username: String, pictureUrl: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.LightGray) // Placeholder avatar
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(text = username, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun GameResultRow(title: String, description: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
