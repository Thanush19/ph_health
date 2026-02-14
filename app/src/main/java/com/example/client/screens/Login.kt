package com.example.client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.example.client.data.model.LoginRequest
import com.example.client.ui.components.ParkingPrimaryButton
import com.example.client.ui.components.ParkingTextField
import com.example.client.viewModels.AuthEvent
import com.example.client.viewModels.AuthUiEvent
import com.example.client.viewModels.AuthViewModel

@Composable
fun Login(
    onSignUpClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val uiModel by viewModel.uiModel.collectAsState(initial = AuthViewModel.InitialUiModel)

    LaunchedEffect(uiModel.event) {
        when (uiModel.event) {
            is AuthUiEvent.NavigateToHome -> {
                onLoginSuccess()
                viewModel.clearNavigationEvent()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            ParkingTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Enter username",
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter password",
                enabled = !uiModel.isLoading,
                visualTransformation = PasswordVisualTransformation()
            )

            if (uiModel.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            ParkingPrimaryButton(
                text = "Sign in",
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        viewModel.takeEvent(
                            AuthEvent.Login(
                                LoginRequest(username = username, password = password)
                            )
                        )
                    }
                },
                enabled = username.isNotBlank() && password.isNotBlank(),
                loading = uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiModel.isLoading
            ) {
                Text(
                    text = "Don't have an account? Sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
