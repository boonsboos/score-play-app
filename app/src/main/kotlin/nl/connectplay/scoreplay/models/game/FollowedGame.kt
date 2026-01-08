package nl.connectplay.scoreplay.models.game

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.leaderboard.LeaderboardEntry

@Serializable
data class FollowedGame(
    val id: Int,
    val scoringMethodId: Int = 1, // defaults to "Highest score wins"
    val name: String,
    val description: String,
    val publisher: String,
    val minPlayers: Int? = null,
    val maxPlayers: Int? = null,
    val duration: Int? = null,
    val minAge: Int? = null,
    val releaseDate: LocalDate? = null,
    val pictures: List<String> = listOf(),
    val podium: List<LeaderboardEntry> = listOf(),
)