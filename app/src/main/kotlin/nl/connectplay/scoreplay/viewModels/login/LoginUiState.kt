package nl.connectplay.scoreplay.viewModels.login

/**
 * Events that the login screen can react to.
 *
 * These are one-time actions. They do not stay in the state.
 * The UI listens for these events and does something once.
 *
 * For example:
 * - Success → go to the next screen.
 */
sealed class LoginEvent {
    object Success : LoginEvent()
}