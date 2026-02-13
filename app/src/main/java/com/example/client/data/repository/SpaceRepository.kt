package com.example.client.data.repository

import com.example.client.data.api.SpaceApiService
import com.example.client.data.model.CreateSpaceRequest
import com.example.client.data.model.ImageUploadResponse
import com.example.client.data.model.SpaceResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class SpaceRepository @Inject constructor(
    private val apiService: SpaceApiService
) {

    suspend fun uploadImage(file: File): Result<ImageUploadResponse> {
        return try {
            val mediaType = "image/*".toMediaTypeOrNull()
            val requestBody = MultipartBody.Part.createFormData(
                "file",
                file.name,
                RequestBody.create(mediaType, file)
            )
            val response = apiService.uploadImage(requestBody)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSpace(request: CreateSpaceRequest): Result<SpaceResponse> {
        return try {
            val response = apiService.createSpace(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Create space failed: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
