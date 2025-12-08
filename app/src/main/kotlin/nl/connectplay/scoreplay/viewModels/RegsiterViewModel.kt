package nl.connectplay.scoreplay.viewModels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.models.auth.register.RegisterRequest

data class RegisterUiState(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val showPassword: Boolean = false,
    val showRepeatPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isFormValid: Boolean =
        email.isNotBlank() &&
                username.isNotBlank() &&
                password.isNotBlank() &&
                repeatPassword.isNotBlank()
}

sealed class RegisterEvent {
    object Success : RegisterEvent()
}

class RegisterViewModel(
    private val authApi: AuthApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // One-shot events like "navigate to login" or "show snackbar"
    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onRepeatPasswordChange(value: String) {
        _uiState.update { it.copy(repeatPassword = value, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onRegisterClick() {
        val state = _uiState.value

        // Check if email is valid
        if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address") }
            return
        }

        // Check if password is at least 8 characters
        if (state.password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 8 characters") }
            return
        }

        // Check if both passwords match
        if (state.password != state.repeatPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val request = RegisterRequest(
                    email = state.email,
                    username = state.username,
                    password = state.password
                )

                // Call to API
                authApi.registerUser(request)

                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.Success)

            } catch (e: ClientRequestException) {
                val status = e.response.status

                val message = when (status) {
                    HttpStatusCode.Conflict -> "User already exists."
                    else -> "Registration failed (${status.value})"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Registration failed"
                    )
                }
            }
        }
    }
}