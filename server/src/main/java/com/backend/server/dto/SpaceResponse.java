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
public class SpaceResponse {

    private Long id;
    private Long ownerId;
    private String address;
    private Integer squareFeet;
    private String vehicleTypes;
    private BigDecimal rentPerHour;
    private BigDecimal rentPerDay;
    private BigDecimal rentMonthly;
    private java.util.List<String> imageUrls;
    private LocalDateTime createdAt;
}
