package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class CreateBookingRequest(
    @SerializedName("slotStart") val slotStart: String,
    @SerializedName("slotEnd") val slotEnd: String
)
