package com.example.client.data.repository

import com.example.client.data.api.BookingApiService
import com.example.client.data.model.BookingResponse
import com.example.client.data.model.CreateBookingRequest
import com.example.client.data.model.DummyPaymentResponse
import javax.inject.Inject

class BookingRepository @Inject constructor(
    private val apiService: BookingApiService
) {

    suspend fun createBooking(spaceId: Long, slotStart: String, slotEnd: String): Result<BookingResponse> {
        return try {
            val request = CreateBookingRequest(slotStart = slotStart, slotEnd = slotEnd)
            val response = apiService.createBooking(spaceId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Booking failed: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun payDummy(bookingId: Long): Result<DummyPaymentResponse> {
        return try {
            val response = apiService.payDummy(bookingId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Payment failed: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
