package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(
    @SerializedName("url") val url: String
)
