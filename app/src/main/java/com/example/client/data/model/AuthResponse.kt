package com.example.client.data.model

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val username: String,
    val email: String,
    val name: String,
    val phone: String,
    val address: String
)
