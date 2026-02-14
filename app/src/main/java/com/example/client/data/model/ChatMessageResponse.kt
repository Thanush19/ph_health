package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessageResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("conversationId") val conversationId: Long,
    @SerializedName("senderId") val senderId: Long,
    @SerializedName("body") val body: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("fromMe") val fromMe: Boolean
)
