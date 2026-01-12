package nl.connectplay.scoreplay.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDto(
    val userId: Int,
    val username: String,
    @SerialName("profilePicture") val picture: String? = null
)