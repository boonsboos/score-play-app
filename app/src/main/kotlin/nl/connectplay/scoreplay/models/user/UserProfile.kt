package nl.connectplay.scoreplay.models.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("userId") val id: Int,
    val username: String,
    val email: String? = null,
    @SerialName("profilePicture") val picture: String? = null
)
