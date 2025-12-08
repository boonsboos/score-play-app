package nl.connectplay.scoreplay.models.search

/**
 * These are the result types of the search
 *
 * A search can be users or games and wil be added in one list
 * Because of the sealed  class Kotlin knows what variants the are
 */
sealed class SearchResult {
    data class UserResult(
        val userId: String,
        val username: String,
        val pictureUrl: String? = null
    ) : SearchResult()

    data class GameResult(
        val gameId: String,
        val title: String,
        val description: String? = null
    ) : SearchResult()
}