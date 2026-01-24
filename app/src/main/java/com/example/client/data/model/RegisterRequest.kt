package com.example.client.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val name: String,
    val phone: String,
    val address: String,
    val password: String
)
