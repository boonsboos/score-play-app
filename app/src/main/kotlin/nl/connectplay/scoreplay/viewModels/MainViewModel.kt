package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import nl.connectplay.scoreplay.stores.TokenDataStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * the ui state for the main screen
 *
 * @property token stored the token or a null
 * @property isLoaded wil be True when the token has finished loading
 */
data class MainUiState(
    val token: String? = null,
    val isLoaded: Boolean = false
)

class MainViewModel(private val tokenDataStore: TokenDataStore) : ViewModel() {

    init {
        loadToken() // load the token as soon the viewModel starts
    }
    
    // give the ui a safe read only version of the data so only viewModel can change it
    private val _tokenState = MutableStateFlow(MainUiState())
    val tokenState = _tokenState.asStateFlow()

    // read the stored token from datastore and update the ui state when a token is found
    private fun loadToken() {
        viewModelScope.launch {
            tokenDataStore.token.collect { storedToken ->
                _tokenState.update {
                    it.copy(
                        token = storedToken,
                        isLoaded = true
                    )
                }
            }
        }
    }

    // delete the token
    fun logout() {
        viewModelScope.launch {
            tokenDataStore.clearToken()
            _tokenState.update {
                it.copy(
                    token = null,
                    isLoaded = true
                )
            }
        }
    }
}