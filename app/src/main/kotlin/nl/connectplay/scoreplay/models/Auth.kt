package nl.connectplay.scoreplay.models
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SignUpRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("userId") val userId: String? = null
)

@Serializable
data class SignInRequest(
    val email: String,
    val username: String,
    val password: String
)