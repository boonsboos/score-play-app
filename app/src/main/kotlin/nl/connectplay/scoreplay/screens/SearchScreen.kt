package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.models.search.SearchFilter
import nl.connectplay.scoreplay.models.search.SearchResult
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.search.SearchListItem
import nl.connectplay.scoreplay.ui.components.search.FilterButton
import nl.connectplay.scoreplay.viewModels.SearchViewModel

@Composable
fun SearchScreen(
    backStack: NavBackStack<NavKey>, // used for navigation history
    modifier: Modifier = Modifier,
    initialQuery: String? = null, // first search string that the screen gets
    searchViewModel: SearchViewModel,
    // callback when a result (user or game) is clicked
    onUserClick: (String) -> Unit = {},
    onGameClick: (String) -> Unit = {}
) {
    // propagate query from Search event to searchViewModel
    // this block will run when the screen opens or when the initialQuery changes
    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            searchViewModel.setQuery(initialQuery)
            searchViewModel.search()
        }
    }

    // with collectAsStateWithLifecycle() the UI will change with the ViewModel (only when visible)
    val filter by searchViewModel.filter.collectAsStateWithLifecycle()
    val results by searchViewModel.results.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ScorePlayTopBar(title = "Search", backStack = backStack) },
        bottomBar = { BottomNavBar(backStack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // space reserved for topbar and bottom bar
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                // this styling is for the filter options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    // ALL FILTER
                    FilterButton(
                        title = "All",
                        selected = filter == SearchFilter.ALL,
                        onClick = {
                            searchViewModel.setFilter(SearchFilter.ALL) // update the filter
                            searchViewModel.search() // update the search results
                        }
                    )
                    // USERS FILTER
                    FilterButton(
                        title = "Users",
                        selected = filter == SearchFilter.USERS,
                        onClick = {
                            searchViewModel.setFilter(SearchFilter.USERS)
                            searchViewModel.search()
                        }
                    )
                    // GAMES FILTER
                    FilterButton(
                        title = "Games",
                        selected = filter == SearchFilter.GAMES,
                        onClick = {
                            searchViewModel.setFilter(SearchFilter.GAMES)
                            searchViewModel.search()
                        }
                    )
                }

                // scrollable list for the found users and games
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(results) { item ->

                        // checks in the list if its a user or game item
                        when (item) {
                            is SearchResult.UserResult -> {
                                SearchListItem(
                                    title = item.username,
                                    subtitle = null, // not needed for user
                                    icon = { Icon(Icons.Filled.Person, "User icon") },
                                    onClick = { onUserClick(item.userId) }
                                )
                            }

                            is SearchResult.GameResult -> {
                                SearchListItem(
                                    title = item.title,
                                    subtitle = item.description,
                                    icon = { Icon(Icons.Filled.Image, "Game icon") },
                                    onClick = { onGameClick(item.gameId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}