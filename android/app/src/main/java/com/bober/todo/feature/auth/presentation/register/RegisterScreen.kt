package com.bober.todo.feature.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bober.todo.ui.theme.TodoappTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegister: (email: String, password: String) -> Unit = { _, _ -> },
    onNavigateToLogin: () -> Unit = {},
) {
    var state by remember { mutableStateOf(RegisterUiState()) }

    RegisterScreenContent(
        state = state,
        onEmailChange = { state = state.copy(email = it, errorMessage = null) },
        onPasswordChange = { state = state.copy(password = it, errorMessage = null) },
        onConfirmPasswordChange = { state = state.copy(confirmPassword = it, errorMessage = null) },
        onTogglePasswordVisible = {
            state = state.copy(isPasswordVisible = !state.isPasswordVisible)
        },
        onToggleConfirmPasswordVisible = {
            state = state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
        },
        onSubmit = {
            when {
                state.email.isBlank() || state.password.isBlank() -> {
                    state = state.copy(errorMessage = "Email and password are required")
                }
                state.password != state.confirmPassword -> {
                    state = state.copy(errorMessage = "Passwords do not match")
                }
                else -> onRegister(state.email.trim(), state.password)
            }
        },
        onNavigateToLogin = onNavigateToLogin,
        modifier = modifier,
    )
}

@Composable
fun RegisterScreenContent(
    state: RegisterUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisible: () -> Unit,
    onToggleConfirmPasswordVisible: () -> Unit,
    onSubmit: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Todoapp",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Create an account to get started.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true,
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            singleLine = true,
            enabled = !state.isLoading,
            visualTransformation = if (state.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = onTogglePasswordVisible, enabled = !state.isLoading) {
                    Text(if (state.isPasswordVisible) "Hide" else "Show")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Confirm password") },
            singleLine = true,
            enabled = !state.isLoading,
            visualTransformation = if (state.isConfirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = onToggleConfirmPasswordVisible, enabled = !state.isLoading) {
                    Text(if (state.isConfirmPasswordVisible) "Hide" else "Show")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp,
                )
            }
            Text(if (state.isLoading) "Creating account…" else "Create account")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !state.isLoading,
        ) {
            Text("Already have an account? Sign in")
        }
    }
}

@Preview(name = "Empty", showBackground = true)
@Composable
private fun RegisterPreviewEmpty() {
    TodoappTheme {
        RegisterScreenContent(
            state = RegisterUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisible = {},
            onToggleConfirmPasswordVisible = {},
            onSubmit = {},
            onNavigateToLogin = {},
        )
    }
}

@Preview(name = "Filled", showBackground = true)
@Composable
private fun RegisterPreviewFilled() {
    TodoappTheme {
        RegisterScreenContent(
            state = RegisterUiState(
                email = "you@example.com",
                password = "Password1!",
                confirmPassword = "Password1!",
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisible = {},
            onToggleConfirmPasswordVisible = {},
            onSubmit = {},
            onNavigateToLogin = {},
        )
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun RegisterPreviewLoading() {
    TodoappTheme {
        RegisterScreenContent(
            state = RegisterUiState(
                email = "you@example.com",
                password = "Password1!",
                confirmPassword = "Password1!",
                isLoading = true,
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisible = {},
            onToggleConfirmPasswordVisible = {},
            onSubmit = {},
            onNavigateToLogin = {},
        )
    }
}

@Preview(name = "Error", showBackground = true)
@Composable
private fun RegisterPreviewError() {
    TodoappTheme {
        RegisterScreenContent(
            state = RegisterUiState(
                email = "you@example.com",
                password = "Password1!",
                confirmPassword = "other",
                errorMessage = "Passwords do not match",
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisible = {},
            onToggleConfirmPasswordVisible = {},
            onSubmit = {},
            onNavigateToLogin = {},
        )
    }
}
