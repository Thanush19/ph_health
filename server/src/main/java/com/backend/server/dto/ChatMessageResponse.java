package com.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private Long senderId;
    /** Plain text (decrypted for API responses). */
    private String body;
    private LocalDateTime sentAt;
    /** True if sender is the current user. */
    private boolean fromMe;
}
