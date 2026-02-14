package com.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponse {

    private Long id;
    private Long spaceId;
    /** Display name for the other party (anonymous: "Space owner" or "Renter"). */
    private String otherPartyDisplayName;
}
