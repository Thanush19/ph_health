package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class ConversationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("spaceId") val spaceId: Long,
    @SerializedName("otherPartyDisplayName") val otherPartyDisplayName: String
)
