package nl.connectplay.scoreplay.viewModels.profile

/**
 * Represents the UI state for asynchronous operations.
 *
 * UiState has three possible states:
 * - Loading: The operation is in progress.
 * - Success: The operation completed successfully and contains the resulting data.
 * - Error: The operation failed, with an error message and an optional exception.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val exception: Exception? = null) : UiState<Nothing>
}