package nl.connectplay.scoreplay.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val data: SignUpRequest? = null,
    val message: String? = null // success or errorMessages
)