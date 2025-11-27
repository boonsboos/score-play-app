package nl.connectplay.scoreplay.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import nl.connectplay.scoreplay.viewModels.LoginViewModel
import nl.connectplay.scoreplay.viewModels.LoginEvent

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit, // used to navigate to the register screen
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle() // Bridge from ViewModel to Compose

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.Success -> onNavigateToRegister()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        // Spacer addes space between ui elements
        Spacer(modifier = Modifier.height(16.dp))

        // Username
        OutlinedTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            singleLine = true,
            visualTransformation =
                if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(), // password is visible or invisible
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onLoginClick() }
            ),
            // show the hide unhide icon
            trailingIcon = {
                IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.showPassword)
                            Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login button
        Button(
            onClick = { viewModel.onLoginClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            // loginbutton is only clickable when username and password are valid and not loading
            enabled = uiState.isFormValid && !uiState.isLoading
        ) {
            Text(if (uiState.isLoading) "Logging in..." else "Login")
        }

        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
        // Navigate to Register
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Dont have an account? Register here")
        }
    }
}
