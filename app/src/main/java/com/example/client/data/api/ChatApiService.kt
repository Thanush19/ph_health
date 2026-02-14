package com.example.client.data.api

import com.example.client.data.model.ChatMessageResponse
import com.example.client.data.model.ConversationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {

    @POST("api/conversations")
    suspend fun getOrCreateConversation(@Body request: GetOrCreateRequest): Response<ConversationResponse>

    @GET("api/conversations/{id}/messages")
    suspend fun getMessages(@Path("id") conversationId: Long): Response<List<ChatMessageResponse>>

    data class GetOrCreateRequest(val spaceId: Long)
}
