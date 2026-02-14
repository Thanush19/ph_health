package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName("likeCount") val likeCount: Long,
    @SerializedName("likedByMe") val likedByMe: Boolean
)
