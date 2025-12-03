package nl.connectplay.scoreplay.viewModels.login

/**
 * Represents all ui data for the login screen
 *
 * When something is changing, the screen wil changes to
 *
 * @property credentials this can be username or email
 * @property password password what the user needs
 * @property showPassword for the toggle of hide or unhide password
 * @property isLoading necessary for the check if we are logging in or not
 * @property errorMessage for the errors
 * @property isFormValid the check for the valid value in the inputfields
 */
data class LoginUiState(
    val credentials: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false
)
