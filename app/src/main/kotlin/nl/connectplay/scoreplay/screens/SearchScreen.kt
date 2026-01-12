package nl.connectplay.scoreplay.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import nl.connectplay.scoreplay.R
import nl.connectplay.scoreplay.models.search.SearchFilter
import nl.connectplay.scoreplay.models.search.SearchResult
import nl.connectplay.scoreplay.ui.components.BottomNavBar
import nl.connectplay.scoreplay.ui.components.FallbackImage
import nl.connectplay.scoreplay.ui.components.ScorePlayTopBar
import nl.connectplay.scoreplay.ui.components.SearchListItem
import nl.connectplay.scoreplay.ui.components.FilterButton
import nl.connectplay.scoreplay.viewModels.SearchViewModel

@Composable
fun SearchScreen(
    backStack: NavBackStack<NavKey>, // used for navigation history
    modifier: Modifier = Modifier,
    initialQuery: String? = null, // first search string that the screen gets
    searchViewModel: SearchViewModel,
    // callback when a result (user or game) is clicked
    onUserClick: (Int) -> Unit = {},
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // this styling is for the filter options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                // ALL FILTER
                FilterButton(
                    title = stringResource(R.string.search_filter_all),
                    selected = filter == SearchFilter.ALL,
                    onClick = {
                        searchViewModel.setFilter(SearchFilter.ALL) // update the filter
                        searchViewModel.search() // update the search results
                    }
                )
                // USERS FILTER
                FilterButton(
                    title = stringResource(R.string.search_filter_users),
                    selected = filter == SearchFilter.USERS,
                    onClick = {
                        searchViewModel.setFilter(SearchFilter.USERS)
                        searchViewModel.search()
                    }
                )
                // GAMES FILTER
                FilterButton(
                    title = stringResource(R.string.search_filter_games),
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
                            Log.d(
                                "SearchScreen",
                                "User ${item.username}, pictureUrl=${item.picture}"
                            )
                            SearchListItem(
                                title = item.username,
                                icon = {
                                    FallbackImage(
                                        url = item.picture,
                                        size = 24.dp,
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                },
                                onClick = { onUserClick(item.userId) }
                            )
                        }

                        is SearchResult.GameResult -> {
                            SearchListItem(
                                title = item.title,
                                subtitle = item.description,
                                icon = {
                                    Icon(
                                        Icons.Filled.Image,
                                        contentDescription = stringResource(R.string.search_description_game_icon)
                                    )
                                },
                                onClick = { onGameClick(item.gameId) }
                            )
                        }
                    }
                }
            }
        }
    }
}