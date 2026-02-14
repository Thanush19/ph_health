package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("spaceId") val spaceId: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("slotStart") val slotStart: String,
    @SerializedName("slotEnd") val slotEnd: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("paymentStatus") val paymentStatus: String,
    @SerializedName("createdAt") val createdAt: String? = null
)
