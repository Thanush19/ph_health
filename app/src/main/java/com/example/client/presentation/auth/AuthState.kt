package com.example.client.presentation.auth

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.client.data.model.AuthResponse

@Stable
internal class AuthState {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var authResponse by mutableStateOf<AuthResponse?>(null)
    var event by mutableStateOf<AuthUiEvent?>(null)

    internal fun toUi(): AuthUiModel {
        return AuthUiModel(
            isLoading = isLoading,
            errorMessage = errorMessage,
            authResponse = authResponse,
            event = event
        )
    }
}

@Immutable
internal data class AuthUiModel(
    val isLoading: Boolean,
    val errorMessage: String?,
    val authResponse: AuthResponse?,
    val event: AuthUiEvent?
)

@Stable
internal sealed interface AuthUiEvent {
    @Immutable
    data object NavigateToHome : AuthUiEvent
}
