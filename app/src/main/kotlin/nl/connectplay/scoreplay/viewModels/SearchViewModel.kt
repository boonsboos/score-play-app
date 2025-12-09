package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.SearchApi
import nl.connectplay.scoreplay.models.search.SearchFilter
import nl.connectplay.scoreplay.models.search.SearchResult

class SearchViewModel(private val searchApi: SearchApi) : ViewModel() {
    // holds the current search results and updates after each API call
    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results = _results.asStateFlow()

    // holds the text the user has typed
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(SearchFilter.ALL) // tracks which filter is active
    val filter = _filter.asStateFlow()


    fun setFilter(newFilter: SearchFilter) {
        _filter.value = newFilter // update the filter so the ViewModel knows which filter is active
    }

    fun setQuery(newQuery: String) {
        _searchQuery.value = newQuery // update the text the user has typed
    }

    fun search() {
        viewModelScope.launch {
            val searchQuery = _searchQuery.value.trim() // clears spaces

            if (searchQuery.isBlank()) {
                _results.value = emptyList()
                return@launch
            }

            val activeFilter = _filter.value

            // choose the search function based on the filter
            val searchResults = when (activeFilter) {
                SearchFilter.ALL -> searchAll(searchQuery)
                SearchFilter.USERS -> searchUsers(searchQuery)
                SearchFilter.GAMES -> searchGames(searchQuery)
            }

            // the result wil be sorted so the better matches wil be on top
            _results.value = sortResults(searchResults, searchQuery)
        }
    }

    private suspend fun searchAll(searchQuery: String): List<SearchResult> {
        val users = searchUsers(searchQuery)
        val games = searchGames(searchQuery)
        return users + games
    }

    private suspend fun searchUsers(searchQuery: String): List<SearchResult.UserResult> {
        return searchApi.searchUsers(searchQuery).map { user ->
            SearchResult.UserResult(
                userId = user.userId,
                username = user.username,
                pictureUrl = user.pictureUrl
            )
        }
    }

    private suspend fun searchGames(searchQuery: String): List<SearchResult.GameResult> {
        return searchApi.searchGames(searchQuery).map { game ->
            SearchResult.GameResult(
                gameId = game.gameId,
                title = game.title,
                description = game.description
            )
        }
    }

    private fun sortResults(
        searchResults: List<SearchResult>,
        searchQuery: String
    ): List<SearchResult> {
        // change everything to lowercase to avoid mismatches with capital letters
        val lowerSearchQuery = searchQuery.lowercase()

        // sort the list using multiple sorting rules
        return searchResults.sortedWith(
            compareBy<SearchResult>(

                // the first sorting rule
                { item ->
                    // get the name or title of the item
                    val name = when (item) {
                        is SearchResult.UserResult -> item.username.lowercase()
                        is SearchResult.GameResult -> item.title.lowercase()
                    }

                    // match the searchResult 0 wil come on top, contains after
                    when {
                        name.startsWith(lowerSearchQuery) -> 0
                        name.contains(lowerSearchQuery) -> 1
                        else -> 2
                    }
                },

                // if the first fails the second sorting rule is alphabetical sorting as a fallback
                { item ->
                    when (item) {
                        is SearchResult.UserResult -> item.username.lowercase()
                        is SearchResult.GameResult -> item.title.lowercase()
                    }
                }
            )
        )
    }
}