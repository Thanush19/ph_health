package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class SpaceResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("ownerId") val ownerId: Long,
    @SerializedName("address") val address: String,
    @SerializedName("squareFeet") val squareFeet: Int,
    @SerializedName("vehicleTypes") val vehicleTypes: String,
    @SerializedName("rentPerHour") val rentPerHour: Double? = null,
    @SerializedName("rentPerDay") val rentPerDay: Double? = null,
    @SerializedName("rentMonthly") val rentMonthly: Double? = null,
    @SerializedName("imageUrls") val imageUrls: List<String>? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)
