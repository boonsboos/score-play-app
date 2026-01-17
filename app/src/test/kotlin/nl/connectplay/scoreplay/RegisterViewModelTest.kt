package nl.connectplay.scoreplay

import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.models.auth.register.RegisterRequest
import nl.connectplay.scoreplay.models.auth.register.RegisterResponse
import nl.connectplay.scoreplay.viewModels.RegisterEvent
import nl.connectplay.scoreplay.viewModels.RegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterViewModelTest {

    // Late-initialized properties for the Auth API and the ViewModel
    private lateinit var authApi: AuthApi
    private lateinit var viewModel: RegisterViewModel

    @Before
    // This function runs before each test, initializing dependencies
    fun setup() {
        // Create a mock instance of the AuthApi
        authApi = mockk()

        // Create a spy of the ViewModel
        // Spying allows real methods to run, but private methods can still be mocked
        viewModel = spyk(RegisterViewModel(authApi), recordPrivateCalls = true)
    }

    @Test
    fun `onRegisterClick emits Success event when registration succeeds`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------

        // Arrange valid credentials
        viewModel.onEmailChange("test@example.com")
        viewModel.onUsernameChange("TestUser")
        viewModel.onPasswordChange("password123")
        viewModel.onRepeatPasswordChange("password123")

        // Mock the private email validation function to always return true
        // Prevents Android-specific Patterns from causing errors in JVM tests
        every { viewModel["isValidEmail"](any<String>()) } returns true

        // Mock the API call to registerUser to return a dummy RegisterRequest
        // This simulates a successful registration without calling the real API
        coEvery { authApi.registerUser(any<RegisterRequest>()) } returns RegisterResponse(
            data = RegisterRequest(
                email = "test@example.com",
                username = "TestUser",
                password = "password123"
            ),
            message = "Registration successful"
        )

        // ------------------------
        // Act
        // ------------------------
        viewModel.onRegisterClick()

        // ------------------------
        // Assert
        // ------------------------

        // Assert: verify that the first event emitted is a success event
        val event = viewModel.events.first()
        assertTrue(event is RegisterEvent.Success)

        // Assert: verify that the UI state is updated correctly
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)      // Loading should be false after success
        assertNull(uiState.errorMessage)    // Error message should be null
    }

    @Test
    fun `onRegisterClick shows error for password mismatch`() = runBlocking {
        // ------------------------
        // Arrange
        // ------------------------
        viewModel.onEmailChange("test@example.com")
        viewModel.onUsernameChange("TestUser")
        viewModel.onPasswordChange("password123")
        viewModel.onRepeatPasswordChange("password456")

        // Mock private email validation so that Android-specific Patterns don't fail in JVM tests
        every { viewModel["isValidEmail"](any<String>()) } returns true

        // ------------------------
        // Act
        // ------------------------
        viewModel.onRegisterClick()

        // ------------------------
        // Assert
        // ------------------------
        val uiState = viewModel.uiState.value
        assertEquals(
            R.string.registration_passwords_not_match,
            uiState.errorMessage
        )
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `onRegisterClick shows error for short password`() = runBlocking {
        // Arrange: set valid email and username
        viewModel.onEmailChange("test@example.com")
        viewModel.onUsernameChange("TestUser")
        viewModel.onPasswordChange("short")            // kort wachtwoord
        viewModel.onRepeatPasswordChange("short")

        every { viewModel["isValidEmail"](any<String>()) } returns true

        // ------------------------
        // Act
        // ------------------------
        viewModel.onRegisterClick()

        // Assert: UI state moet foutmelding hebben voor korte password
        val uiState = viewModel.uiState.value

        // Check exact dat het de "wachtwoord te kort" fout is
        assertEquals(
            R.string.registration_invalid_password,
            uiState.errorMessage
        )
    }
}
