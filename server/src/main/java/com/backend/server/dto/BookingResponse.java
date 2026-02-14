package com.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long spaceId;
    private Long userId;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
