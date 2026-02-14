package com.backend.server.controller;

import com.backend.server.dto.ChatMessageResponse;
import com.backend.server.dto.ConversationResponse;
import com.backend.server.service.ChatService;
import com.backend.server.service.ConversationService;
import com.backend.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;
    private final ChatService chatService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ConversationResponse> getOrCreate(@RequestBody GetOrCreateRequest request) {
        ConversationResponse response = conversationService.getOrCreate(request.getSpaceId(), getCurrentUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long id) {
        Long userId = userService.getUserIdByUsername(getCurrentUsername());
        return ResponseEntity.ok(chatService.getMessages(id, userId));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return auth.getName();
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetOrCreateRequest {
        private Long spaceId;
    }
}
