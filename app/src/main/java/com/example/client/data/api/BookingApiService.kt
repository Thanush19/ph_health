package com.example.client.data.api

import com.example.client.data.model.BookingResponse
import com.example.client.data.model.CreateBookingRequest
import com.example.client.data.model.DummyPaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApiService {

    @POST("api/spaces/{spaceId}/book")
    suspend fun createBooking(
        @Path("spaceId") spaceId: Long,
        @Body request: CreateBookingRequest
    ): Response<BookingResponse>

    @POST("api/bookings/{bookingId}/pay-dummy")
    suspend fun payDummy(@Path("bookingId") bookingId: Long): Response<DummyPaymentResponse>
}
