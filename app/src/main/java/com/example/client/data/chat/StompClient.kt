package com.example.client.data.chat

import com.example.client.data.model.ChatMessageResponse
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * Minimal STOMP-over-WebSocket client for Spring SockJS endpoint.
 * Connects to ws://host/ws/websocket, sends STOMP CONNECT with JWT, subscribes to topic, sends SEND.
 */
class StompClient(
    private val baseUrl: String,
    private val token: String?,
    private val gson: Gson
) {
    private var webSocket: WebSocket? = null
    private val _incomingMessages = Channel<ChatMessageResponse>(Channel.UNLIMITED)
    val incomingMessages: Flow<ChatMessageResponse> = _incomingMessages.receiveAsFlow()

    private val wsUrl: String
        get() {
            val base = baseUrl.replace("http://", "ws://").replace("https://", "wss://").trimEnd('/')
            return "$base/ws/websocket"
        }

    fun connect(conversationId: Long, onConnected: () -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url(wsUrl)
            .apply { if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token") }
            .build()
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Wait for SockJS "o" frame before sending STOMP CONNECT
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                when {
                    text == "o" -> sendStompConnect() // SockJS open
                    text.startsWith("CONNECTED") -> {
                        sendStompSubscribe(conversationId)
                        onConnected()
                    }
                    text.startsWith("MESSAGE") -> {
                        parseMessageFrame(text)?.let { _incomingMessages.trySend(it) }
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError(t.message ?: "Connection failed")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {}
            override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {}
        })
    }

    private fun sendStompConnect() {
        val authLine = if (!token.isNullOrBlank()) "Authorization:Bearer $token\n" else ""
        val frame = "CONNECT\n${authLine}accept-version:1.1,1.2\nhost:localhost\n\n\u0000"
        webSocket?.send(frame)
    }

    private fun sendStompSubscribe(conversationId: Long) {
        val frame = "SUBSCRIBE\nid:sub-1\ndestination:/topic/conversation/$conversationId\n\n\u0000"
        webSocket?.send(frame)
    }

    fun sendMessage(conversationId: Long, text: String) {
        val payload = """{"conversationId":$conversationId,"text":${gson.toJson(text)}}"""
        val frame = "SEND\ndestination:/app/chat\ncontent-type:application/json\n\n$payload\u0000"
        webSocket?.send(frame)
    }

    private fun parseMessageFrame(frame: String): ChatMessageResponse? {
        return try {
            val bodyStart = frame.indexOf("\n\n") + 2
            val body = frame.substring(bodyStart).trimEnd('\u0000')
            gson.fromJson(body, ChatMessageResponse::class.java)
        } catch (_: Exception) {
            null
        }
    }

    fun disconnect() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}
