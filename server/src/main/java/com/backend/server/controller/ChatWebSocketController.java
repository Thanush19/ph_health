package com.backend.server.controller;

import com.backend.server.dto.ChatMessageResponse;
import com.backend.server.service.ChatService;
import com.backend.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void sendMessage(@Payload SendMessagePayload payload, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        Long senderId = userService.getUserIdByUsername(authentication.getName());
        ChatMessageResponse response = chatService.sendMessage(payload.getConversationId(), senderId, payload.getText());
        messagingTemplate.convertAndSend("/topic/conversation/" + payload.getConversationId(), response);
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SendMessagePayload {
        private Long conversationId;
        private String text;
    }
}
