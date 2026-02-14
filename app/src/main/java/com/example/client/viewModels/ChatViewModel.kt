package com.example.client.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.data.chat.StompClient
import com.example.client.data.model.ChatMessageResponse
import com.example.client.data.model.ConversationResponse
import com.example.client.data.model.SpaceResponse
import com.example.client.data.local.TokenManager
import com.example.client.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.gson.Gson
import javax.inject.Inject

private const val BASE_URL = "http://10.0.2.2:8080/"

data class ChatUiState(
    val conversation: ConversationResponse? = null,
    val messages: List<ChatMessageResponse> = emptyList(),
    val inputText: String = "",
    val loading: Boolean = false,
    val connected: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var stompClient: StompClient? = null
    private var currentConversationId: Long = 0L

    fun setSpace(space: SpaceResponse) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            chatRepository.getOrCreateConversation(space.id)
                .onSuccess { conv ->
                    _uiState.value = _uiState.value.copy(
                        conversation = conv,
                        loading = false,
                        error = null
                    )
                    currentConversationId = conv.id
                    loadMessages(conv.id)
                    connectWebSocket(conv.id)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = it.message
                    )
                }
        }
    }

    private fun loadMessages(conversationId: Long) {
        viewModelScope.launch {
            chatRepository.getMessages(conversationId)
                .onSuccess { list ->
                    _uiState.update { it.copy(messages = list) }
                }
                .onFailure { }
        }
    }

    private fun connectWebSocket(conversationId: Long) {
        val token = tokenManager.getToken()
        val client = StompClient(BASE_URL, token, Gson())
        stompClient = client
        viewModelScope.launch {
            client.incomingMessages.collect { msg ->
                _uiState.update { state ->
                    if (state.messages.any { it.id == msg.id }) state
                    else state.copy(messages = state.messages + msg)
                }
            }
        }
        client.connect(
            conversationId = conversationId,
            onConnected = { _uiState.update { it.copy(connected = true, error = null) } },
            onError = { err -> _uiState.update { it.copy(error = err, connected = false) } }
        )
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || currentConversationId == 0L) return
        stompClient?.sendMessage(currentConversationId, text)
        _uiState.value = _uiState.value.copy(inputText = "")
    }

    override fun onCleared() {
        super.onCleared()
        stompClient?.disconnect()
    }
}
