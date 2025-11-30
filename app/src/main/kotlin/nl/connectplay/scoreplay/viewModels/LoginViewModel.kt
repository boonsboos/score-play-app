package nl.connectplay.scoreplay.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.auth.AuthApi
import nl.connectplay.scoreplay.api.auth.request.LoginRequest
import nl.connectplay.scoreplay.data.TokenDataStore

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

class LoginViewModel(
    private val authApi: AuthApi,
    private val appContext: Context
) : ViewModel() {

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
            _uiState.update { it.copy(errorMessage = "All fields must be filled in.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val response = authApi.login(
                    LoginRequest(
                        username = state.username,
                        password = state.password
                    )
                )

                TokenDataStore.saveToken(appContext, response.token)

                _uiState.update { it.copy(isLoading = false) }

                _events.send(LoginEvent.Success)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Login failed. Please check your username or password."
                    )
                }
            }
        }
    }
}
