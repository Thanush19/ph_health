package com.backend.server.service;

import com.backend.server.dto.ChatMessageResponse;
import com.backend.server.entity.ChatMessage;
import com.backend.server.repository.ChatMessageRepository;
import com.backend.server.util.MessageEncryption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationService conversationService;
    private final MessageEncryption messageEncryption;

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long conversationId, Long currentUserId) {
        if (!conversationService.isParticipant(conversationId, currentUserId)) {
            throw new IllegalArgumentException("Not a participant of this conversation");
        }
        return chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(m -> toResponse(m, currentUserId))
                .toList();
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long conversationId, Long senderId, String plainText) {
        if (!conversationService.isParticipant(conversationId, senderId)) {
            throw new IllegalArgumentException("Not a participant of this conversation");
        }
        String encrypted = messageEncryption.encrypt(plainText);
        ChatMessage msg = ChatMessage.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .bodyEncrypted(encrypted)
                .build();
        msg = chatMessageRepository.save(msg);
        return toResponse(msg, senderId);
    }

    public String decryptBody(String encryptedBody) {
        return messageEncryption.decrypt(encryptedBody);
    }

    private ChatMessageResponse toResponse(ChatMessage m, long currentUserId) {
        String body = messageEncryption.decrypt(m.getBodyEncrypted());
        return ChatMessageResponse.builder()
                .id(m.getId())
                .conversationId(m.getConversationId())
                .senderId(m.getSenderId())
                .body(body)
                .sentAt(m.getSentAt())
                .fromMe(m.getSenderId().equals(currentUserId))
                .build();
    }
}
