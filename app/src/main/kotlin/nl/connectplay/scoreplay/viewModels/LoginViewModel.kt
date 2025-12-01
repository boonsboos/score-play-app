package nl.connectplay.scoreplay.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.models.auth.request.LoginRequest
import nl.connectplay.scoreplay.stores.TokenDataStore

data class LoginUiState(
    val credentials: String = "",
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
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    private val _events = MutableSharedFlow<LoginEvent>()
    val uiState = _uiState.asStateFlow()
    val events = _events.asSharedFlow()

    fun onCredentialsChange(value: String) {
        _uiState.update {
            it.copy(
                credentials = value,
                isFormValid = value.isNotBlank() && it.password.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                isFormValid = it.credentials.isNotBlank() && value.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun onLoginClick() {
        val state = _uiState.value

        if (state.credentials.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields must be filled in.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val inputCredentials = _uiState.value.credentials
                val isEmail = inputCredentials.contains("@")

                val response = authApi.login(
                    LoginRequest(
                        email = if (isEmail) inputCredentials else null,
                        username = if (!isEmail) inputCredentials else null,
                        password = state.password
                    )
                )

                tokenDataStore.saveToken(response.token)

                _uiState.update { it.copy(isLoading = false) }

                _events.emit(LoginEvent.Success)

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
