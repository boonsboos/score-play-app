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
    // holds the current search results and updateds after each API call
    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results = _results.asStateFlow()

    // holds the text the user has typed
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filter = MutableStateFlow(SearchFilter.ALL) // tracks witch filter is active
    val filter = _filter.asStateFlow()


    fun setFilter(newFilter: SearchFilter) {
        _filter.value = newFilter // update the filter so the ViewModel knows witch filter is active
    }

    fun setQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun search() {
        viewModelScope.launch {
            val queryValue = _searchQuery.value

            // if the user enters no value or only space, dont search
            // clear the results list if the query is empty
            if (queryValue.isBlank()) {
                _results.value = emptyList()
                return@launch // stop the coroutine early
            }
            val activeFilter = _filter.value

            when (activeFilter) {
                SearchFilter.ALL -> {
                    val users = searchApi.searchUsers(queryValue)
                    val games = searchApi.searchGames(queryValue)

                    // put both game and user results in one list
                    val combinedSearchList = buildList {
                        users.forEach { user ->
                            add(
                                SearchResult.UserResult(
                                    user.userId,
                                    user.username,
                                    user.pictureUrl
                                )
                            )
                        }
                        games.forEach { game ->
                            add(SearchResult.GameResult(game.gameId, game.title, game.description))
                        }
                    }
                    _results.value = combinedSearchList
                }

                SearchFilter.USERS -> {
                    val users = searchApi.searchUsers(queryValue)

                    val usersList = buildList {
                        users.forEach { user ->
                            add(
                                SearchResult.UserResult(
                                    user.userId,
                                    user.username,
                                    user.pictureUrl
                                )
                            )
                        }
                    }
                    _results.value = usersList
                }

                SearchFilter.GAMES -> {
                    val games = searchApi.searchGames(queryValue)

                    val gamesList = buildList {
                        games.forEach { game ->
                            add(SearchResult.GameResult(game.gameId, game.title, game.description))
                        }
                    }
                    _results.value = gamesList
                }
            }
        }
    }
}
