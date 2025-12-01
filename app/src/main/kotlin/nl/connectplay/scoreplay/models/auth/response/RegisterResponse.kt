package nl.connectplay.scoreplay.models.auth.response

import kotlinx.serialization.Serializable
import nl.connectplay.scoreplay.models.auth.request.SignUpRequest

@Serializable
data class RegisterResponse(
    val data: SignUpRequest? = null,
    val message: String? = null // success or errorMessages
)