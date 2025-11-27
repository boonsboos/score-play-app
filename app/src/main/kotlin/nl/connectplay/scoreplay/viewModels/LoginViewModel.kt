package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false
)

sealed class LoginEvent {
    object Success : LoginEvent()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val uiState = _uiState.asStateFlow()
    val events = _events.receiveAsFlow()

    fun onUsernameChange(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                isFormValid = value.isNotBlank() && it.password.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                isFormValid = it.username.isNotBlank() && value.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All field must be filed in") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // TODO(hier komt de api call)
            kotlinx.coroutines.delay(1000)

            _uiState.update { it.copy(isLoading = false) }

            _events.send(LoginEvent.Success)
        }
    }
}


