package com.example.client.data.api

import com.example.client.data.model.CreateSpaceRequest
import com.example.client.data.model.ImageUploadResponse
import com.example.client.data.model.SpaceResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface SpaceApiService {

    @GET("api/spaces")
    suspend fun getSpaces(): Response<List<SpaceResponse>>

    @GET("api/spaces/mine")
    suspend fun getMySpaces(): Response<List<SpaceResponse>>

    @GET("api/spaces/{id}")
    suspend fun getSpaceById(@Path("id") id: Long): Response<SpaceResponse>

    @Multipart
    @POST("api/spaces/upload-image")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ImageUploadResponse>

    @POST("api/spaces")
    suspend fun createSpace(@Body request: CreateSpaceRequest): Response<SpaceResponse>

    @PUT("api/spaces/{id}")
    suspend fun updateSpace(@Path("id") id: Long, @Body request: CreateSpaceRequest): Response<SpaceResponse>
}
