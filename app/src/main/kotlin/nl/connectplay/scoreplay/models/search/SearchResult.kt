package nl.connectplay.scoreplay.models.search

sealed class SearchResult {
    data class UserResult(
        val userId: String,
        val username: String,
        val pictureUrl: String? = null
    ) : SearchResult()

    data class GameResult(val gameId: String, val title: String, val description: String? = null) :
        SearchResult()
}