package nl.connectplay.scoreplay

import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nl.connectplay.scoreplay.api.AuthApi
import nl.connectplay.scoreplay.models.auth.register.RegisterRequest
import nl.connectplay.scoreplay.viewModels.RegisterEvent
import nl.connectplay.scoreplay.viewModels.RegisterUiState
import nl.connectplay.scoreplay.viewModels.RegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for RegisterViewModel
 */
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
        // Arrange: set valid email in the ViewModel
        viewModel.onEmailChange("test@example.com")

        // Arrange: set valid username
        viewModel.onUsernameChange("TestUser")
        viewModel.onPasswordChange("password123")
        viewModel.onRepeatPasswordChange("password123")

        // Mock the private email validation function to always return true
        // Prevents Android-specific Patterns from causing errors in JVM tests
        every { viewModel["isValidEmail"](any<String>()) } returns true

        // Mock the API call to registerUser to return a dummy RegisterRequest
        // This simulates a successful registration without calling the real API
        coEvery { authApi.registerUser(any<RegisterRequest>()) } returns RegisterRequest(
            email = "test@example.com",
            username = "TestUser",
            password = "password123"
        )

        // Act: call the ViewModel's register function
        viewModel.onRegisterClick()

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
        // Arrange: set valid email and username
        viewModel.onEmailChange("test@example.com")
        viewModel.onUsernameChange("TestUser")

        // Arrange: set mismatched passwords
        viewModel.onPasswordChange("password123")
        viewModel.onRepeatPasswordChange("password456")

        // Mock private email validation to return true
        every { viewModel["isValidEmail"](any<String>()) } returns true

        // Act: attempt to register
        viewModel.onRegisterClick()

        // Assert: verify error for password mismatch
        val uiState: RegisterUiState = viewModel.uiState.value
        assertEquals("Passwords do not match", uiState.errorMessage)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `onRegisterClick shows error for short password`() = runBlocking {
        // Arrange: set valid email and username
        viewModel.onEmailChange("test@example.com")
        viewModel.onUsernameChange("TestUser")
        viewModel.onPasswordChange("short")
        viewModel.onRepeatPasswordChange("short")

        // Mock private email validation to always return true
        every { viewModel["isValidEmail"](any<String>()) } returns true

        // Act: attempt to register
        viewModel.onRegisterClick()

        // Assert: verify error for short password
        val uiState: RegisterUiState = viewModel.uiState.value
        assertEquals("Password must be at least 8 characters", uiState.errorMessage)
        assertFalse(uiState.isLoading)
    }
}
