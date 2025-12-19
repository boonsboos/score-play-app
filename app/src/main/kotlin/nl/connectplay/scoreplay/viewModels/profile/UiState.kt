package nl.connectplay.scoreplay.viewModels.profile

/**
 * Represents the UI state for asynchronous operations.
 *
 * UiState has the following possible states:
 * - [Idle]: The operation has not started yet.
 * - [Initial]: The operation has initial data.
 * - [Loading]: The operation is in progress.
 * - [Success]: The operation completed successfully and contains the resulting data.
 * - [Error]: The operation failed, with an error message and an optional exception.
 */
sealed interface UiState<out T> {
    /**
     * This represents the idle state where no operation has started yet
     * and no data is available.
     *
     * Useful as a default state before any data is loaded.
     */
    data object Idle : UiState<Nothing>

    /**
     * This represents the initial state with already available data,
     * before starting a new operation.
     */
    data class Initial<T>(val data: T) : UiState<T>

    /**
     * This represents the loading state where an operation is currently in progress.
     * Useful for showing loading indicators while waiting for an operation to complete.
     */
    data object Loading : UiState<Nothing>

    /**
     * This represents the success state where an operation has completed successfully.
     * Contains the resulting data from the operation.
     */
    data class Success<T>(val data: T) : UiState<T>

    /**
     * This represents the error state where an operation has failed.
     * Contains an error message and an optional exception for debugging.
     */
    data class Error(val message: String, val exception: Exception? = null) : UiState<Nothing>
}