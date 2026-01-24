package com.example.client.presentation.auth

import com.example.client.data.model.LoginRequest
import com.example.client.data.model.RegisterRequest

sealed class AuthEvent {
    data class Register(val request: RegisterRequest) : AuthEvent()
    data class Login(val request: LoginRequest) : AuthEvent()
    object ClearError : AuthEvent()
}
