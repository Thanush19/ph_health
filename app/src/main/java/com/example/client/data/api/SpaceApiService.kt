package com.example.client.data.api

import com.example.client.data.model.CreateSpaceRequest
import com.example.client.data.model.ImageUploadResponse
import com.example.client.data.model.SpaceResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SpaceApiService {

    @Multipart
    @POST("api/spaces/upload-image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageUploadResponse>

    @POST("api/spaces")
    suspend fun createSpace(@Body request: CreateSpaceRequest): Response<SpaceResponse>
}
