package nl.connectplay.scoreplay.viewModels.main

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