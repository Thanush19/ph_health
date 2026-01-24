package com.example.client.data.repository

import com.example.client.data.api.AuthApiService
import com.example.client.data.model.AuthResponse
import com.example.client.data.model.LoginRequest
import com.example.client.data.model.RegisterRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: AuthApiService
) {

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Registration failed: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = apiService.login(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Login failed: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
