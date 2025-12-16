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
    var minPlayers: Int?,
    var maxPlayers: Int?,
    var duration: Int?,
    var minAge: Int?,
    var releaseDate: LocalDate?,
    var pictures: List<String> = listOf()
)