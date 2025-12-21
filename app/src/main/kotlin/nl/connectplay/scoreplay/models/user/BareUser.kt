package nl.connectplay.scoreplay.models.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BareUser(
    @SerialName("userId") val id: Int,
    val username: String,
    @SerialName("profilePicture") val picture: String? = null
)
