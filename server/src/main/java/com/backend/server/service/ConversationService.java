package com.backend.server.service;

import com.backend.server.dto.ConversationResponse;
import com.backend.server.entity.Conversation;
import com.backend.server.repository.ConversationRepository;
import com.backend.server.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserService userService;

    /**
     * Get or create a conversation between the current user (renter) and the space owner.
     * Current user must not be the space owner.
     */
    @Transactional
    public ConversationResponse getOrCreate(Long spaceId, String username) {
        if (!parkingSpaceRepository.existsById(spaceId)) {
            throw new IllegalArgumentException("Space not found: " + spaceId);
        }
        Long renterId = userService.getUserIdByUsername(username);
        var space = parkingSpaceRepository.findById(spaceId).orElseThrow();
        Long ownerId = space.getOwnerId();
        if (ownerId.equals(renterId)) {
            throw new IllegalArgumentException("Owner cannot start a chat with themselves");
        }

        Conversation conv = conversationRepository.findBySpaceIdAndRenterId(spaceId, renterId)
                .orElseGet(() -> conversationRepository.save(Conversation.builder()
                        .spaceId(spaceId)
                        .ownerId(ownerId)
                        .renterId(renterId)
                        .build()));

        return ConversationResponse.builder()
                .id(conv.getId())
                .spaceId(conv.getSpaceId())
                .otherPartyDisplayName("Space owner")
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isParticipant(Long conversationId, Long userId) {
        return conversationRepository.findById(conversationId)
                .filter(c -> c.getOwnerId().equals(userId) || c.getRenterId().equals(userId))
                .isPresent();
    }
}
