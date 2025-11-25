package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    // private val authRepository: AuthRepository // TODO: authentication
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // One-shot events like "navigate to login" or "show snackbar"
    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage =  null) }
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

    fun onRegisterClick() {
        val state = _uiState.value

        // Base validation in ViewModel
        if (state.password != state.repeatPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // authRepository.register(state.email, state.username, state.password)

                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.Success)
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = t.message ?: "Registration failed"
                    )
                }
            }
        }
    }
}