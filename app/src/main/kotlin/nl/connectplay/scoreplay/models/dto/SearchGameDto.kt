package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchGameDto(
    // we added @SerialName so we can use our own property names
    @SerialName("id")
    val gameId: String,
    @SerialName("name")
    val title: String,
    val description: String?
)