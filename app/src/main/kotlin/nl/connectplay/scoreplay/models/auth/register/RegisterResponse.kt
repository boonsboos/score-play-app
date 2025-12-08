package nl.connectplay.scoreplay.models.auth.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val data: RegisterRequest? = null,
    val message: String? = null // success or errorMessages
)