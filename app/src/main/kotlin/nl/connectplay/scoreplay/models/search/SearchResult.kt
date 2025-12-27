package nl.connectplay.scoreplay.models.search

/**
 * These are the result types of the search
 *
 * A search can be users or games and will be added in one list
 * Because of the sealed class Kotlin knows what variants there are
 */
sealed class SearchResult {
    data class UserResult(
        val userId: Int,
        val username: String,
        val pictureUrl: String? = null
    ) : SearchResult()

    data class GameResult(
        val gameId: String,
        val title: String,
        val description: String? = null
    ) : SearchResult()
}