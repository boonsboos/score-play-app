package nl.connectplay.scoreplay.models.auth.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String, val userId: Int)