package nl.connectplay.scoreplay.viewModels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.models.auth.login.LoginRequest
import nl.connectplay.scoreplay.stores.TokenDataStore
import nl.connectplay.scoreplay.stores.UserDataStore

/**
 * Handles what the login screen needs
 *
 * It will check the input, save the token and performs API-calls
 * and tells the UI what to do
 *
 */
class LoginViewModel(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    private val _events = MutableSharedFlow<LoginEvent>()

    // these two values are read-only versions for the UI
    val uiState = _uiState.asStateFlow()
    val events = _events.asSharedFlow()

    // called when the user types username/email
    fun onCredentialsChange(value: String) {
        _uiState.update {
            it.copy(
                credentials = value,
                isFormValid = value.isNotBlank() && it.password.isNotBlank(),
                errorMessage = null
            )
        }
    }

    // called when the user types password
    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                isFormValid = it.credentials.isNotBlank() && value.isNotBlank(),
                errorMessage = null
            )
        }
    }

    // for the hide or unhide the password
    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    // called when the user presses the login button
    fun onLoginClick() {
        val state = _uiState.value

        // check if the fields are filed and stop if empty
        if (state.credentials.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "All fields must be filled in.") }
            return
        }

        // handle login inside a coroutine in the background
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val inputCredentials = _uiState.value.credentials
                val isEmail = inputCredentials.contains("@")

                // make the login request using email or username
                val response = authApi.login(
                    LoginRequest(
                        email = if (isEmail) inputCredentials else null,
                        username = if (!isEmail) inputCredentials else null,
                        password = state.password
                    )
                )

                // this saves the token so when de user close the app the token wil be used when de app is back online
                tokenDataStore.saveToken(response.token)

                _uiState.update { it.copy(isLoading = false) }

                // tells the ui login was success
                _events.emit(LoginEvent.Success)

            } catch (e: Exception) {
                // show the error message if login failed
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
