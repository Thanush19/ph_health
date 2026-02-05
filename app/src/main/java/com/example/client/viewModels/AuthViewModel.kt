package com.example.client.viewModels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.example.client.data.local.TokenManager
import com.example.client.data.model.AuthResponse
import com.example.client.data.model.LoginRequest
import com.example.client.data.model.RegisterRequest
import com.example.client.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val presenter: AuthPresenter
) : ViewModel() {
    val uiModel: StateFlow<AuthUiModel> = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        presenter.generateUi()
    }

    fun takeEvent(event: AuthEvent) {
        presenter.onInteraction(event)
    }

    fun clearNavigationEvent() {
        presenter.clearNavigationEvent()
    }

    companion object {
        val InitialUiModel = AuthUiModel(isLoading = false, errorMessage = null, authResponse = null, event = null)
    }
}

 class AuthPresenter @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) {
    private val events = Channel<AuthEvent>(5)
    private val state = AuthState()

    fun onInteraction(event: AuthEvent) {
        events.trySend(event)
    }

    fun clearNavigationEvent() {
        state.event = null
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

}

sealed class AuthEvent {
    data class Register(val request: RegisterRequest) : AuthEvent()
    data class Login(val request: LoginRequest) : AuthEvent()
}



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
data class AuthUiModel(
    val isLoading: Boolean,
    val errorMessage: String?,
    val authResponse: AuthResponse?,
    val event: AuthUiEvent?
)

@Stable
sealed interface AuthUiEvent {
    @Immutable
    data object NavigateToHome : AuthUiEvent
}
