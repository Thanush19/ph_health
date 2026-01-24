package com.example.client.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import com.example.client.data.local.TokenManager
import com.example.client.data.model.LoginRequest
import com.example.client.data.model.RegisterRequest
import com.example.client.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val presenter: AuthPresenter
) : ViewModel() {

    fun takeEvent(event: AuthEvent) {
        presenter.onInteraction(event)
    }
}

 class AuthPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    private val events = Channel<AuthEvent>(Channel.UNLIMITED)
    private val state = AuthState()

    fun onInteraction(event: AuthEvent) {
        events.trySend(event)
    }

    @Composable
    internal fun generateUi(): AuthUiModel {
        HandleEvents()
        return state.toUi()
    }

    @Composable
    private fun HandleEvents() {
        LaunchedEffect(Unit) {
            events.receiveAsFlow().collect { event ->
                when (event) {
                    is AuthEvent.Register -> handleRegister(event.request)
                    is AuthEvent.Login -> handleLogin(event.request)
                    is AuthEvent.ClearError -> handleClearError()
                }
            }
        }
    }

    private suspend fun handleRegister(request: RegisterRequest) {
        state.isLoading = true
        state.errorMessage = null
        state.event = null

        authRepository.register(request)
            .onSuccess { response ->
                tokenManager.saveToken(response.token)
                state.authResponse = response
                state.isLoading = false
                state.event = AuthUiEvent.NavigateToHome
            }
            .onFailure { exception ->
                state.errorMessage = exception.message ?: "Registration failed"
                state.isLoading = false
            }
    }

    private suspend fun handleLogin(request: LoginRequest) {
        state.isLoading = true
        state.errorMessage = null
        state.event = null

        authRepository.login(request)
            .onSuccess { response ->
                tokenManager.saveToken(response.token)
                state.authResponse = response
                state.isLoading = false
                state.event = AuthUiEvent.NavigateToHome
            }
            .onFailure { exception ->
                state.errorMessage = exception.message ?: "Login failed"
                state.isLoading = false
            }
    }

    private fun handleClearError() {
        state.errorMessage = null
    }
}
