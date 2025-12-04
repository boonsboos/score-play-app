package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchGameDto(
    @SerialName("id")
    val gameId: String,
    @SerialName("name")
    val title: String,
    val description: String?
)