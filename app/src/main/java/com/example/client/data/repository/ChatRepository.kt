package com.example.client.data.repository

import com.example.client.data.api.ChatApiService
import com.example.client.data.model.ChatMessageResponse
import com.example.client.data.model.ConversationResponse
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val apiService: ChatApiService
) {

    suspend fun getOrCreateConversation(spaceId: Long): Result<ConversationResponse> {
        return try {
            val response = apiService.getOrCreateConversation(ChatApiService.GetOrCreateRequest(spaceId))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to start conversation: ${response.errorBody()?.string() ?: "Unknown error"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(conversationId: Long): Result<List<ChatMessageResponse>> {
        return try {
            val response = apiService.getMessages(conversationId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to load messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
