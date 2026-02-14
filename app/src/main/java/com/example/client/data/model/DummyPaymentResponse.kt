package com.example.client.data.model

import com.google.gson.annotations.SerializedName

data class DummyPaymentResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("transactionId") val transactionId: String? = null,
    @SerializedName("message") val message: String? = null
)
