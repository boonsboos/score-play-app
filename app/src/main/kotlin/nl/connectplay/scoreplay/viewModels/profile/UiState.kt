package nl.connectplay.scoreplay.viewModels.profile

/**
 * explanations needed
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val exception: Exception? = null) : UiState<Nothing>
}