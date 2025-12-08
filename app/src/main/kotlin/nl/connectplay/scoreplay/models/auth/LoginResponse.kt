package nl.connectplay.scoreplay.models.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String, val userId: Int)