package com.example.client.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.RegisterRequest
import com.example.client.ui.components.ParkingPrimaryButton
import com.example.client.ui.components.ParkingTextField
import com.example.client.viewModels.AuthEvent
import com.example.client.viewModels.AuthUiEvent
import com.example.client.viewModels.AuthViewModel

@Composable
fun Register(
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }

    val uiModel by viewModel.uiModel.collectAsState(initial = AuthViewModel.InitialUiModel)

    LaunchedEffect(uiModel.event) {
        when (uiModel.event) {
            is AuthUiEvent.NavigateToHome -> {
                onRegisterSuccess()
                viewModel.clearNavigationEvent()
            }
            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create account",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(40.dp))

            ParkingTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full name",
                placeholder = "Your name",
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Choose a username",
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "your@email.com",
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone",
                placeholder = "Phone number",
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address",
                placeholder = "Your address",
                singleLine = false,
                maxLines = 3,
                enabled = !uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            ParkingTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Create a password",
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
                text = "Sign up",
                onClick = {
                    if (name.isNotBlank() && username.isNotBlank() && email.isNotBlank() &&
                        phone.isNotBlank() && address.isNotBlank() && password.isNotBlank()
                    ) {
                        viewModel.takeEvent(
                            AuthEvent.Register(
                                RegisterRequest(
                                    username = username,
                                    email = email,
                                    name = name,
                                    phone = phone,
                                    address = address,
                                    password = password
                                )
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && username.isNotBlank() && email.isNotBlank() &&
                    phone.isNotBlank() && address.isNotBlank() && password.isNotBlank(),
                loading = uiModel.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiModel.isLoading
            ) {
                Text(
                    text = "Already have an account? Sign in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
