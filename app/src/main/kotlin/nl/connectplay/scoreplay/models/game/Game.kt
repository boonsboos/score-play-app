package nl.connectplay.scoreplay.models.game

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Int,
    val scoringMethodId: Int,
    val name: String,
    val description: String,
    val publisher: String,
    var minPlayers: Int? = null,
    var maxPlayers: Int? = null,
    var duration: Int? = null,
    var minAge: Int? = null,
    var releaseDate: LocalDate? = null,
    var pictures: List<String> = listOf()
)