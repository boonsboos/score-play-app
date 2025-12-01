package nl.connectplay.scoreplay.viewModels

import androidx.lifecycle.ViewModel
import nl.connectplay.scoreplay.stores.TokenDataStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val token: String? = null,
    val isLoaded: Boolean = false
)

class MainViewModel(private val tokenDataStore: TokenDataStore) : ViewModel() {

    init {
        loadToken()
    }

    private val _tokenState = MutableStateFlow(MainUiState())
    val tokenState = _tokenState.asStateFlow()

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