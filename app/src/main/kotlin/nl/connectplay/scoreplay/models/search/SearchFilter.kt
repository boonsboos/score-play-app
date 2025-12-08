package nl.connectplay.scoreplay.models.search

/**
 * Represents the type of search the user wants to perform.
 *
 * The types are used for the filters when the search is performed, and update the search
 * - [ALL]
 * - [USERS]
 * - [GAMES]
 *
 */
enum class SearchFilter {
    ALL,
    USERS,
    GAMES
}