package com.backend.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSpaceRequest {

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Square feet is required")
    @Positive(message = "Square feet must be positive")
    private Integer squareFeet;

    @NotBlank(message = "Vehicle types are required (e.g. Car, Bike, SUV)")
    private String vehicleTypes;

    @PositiveOrZero(message = "Rent per hour must be 0 or positive")
    private BigDecimal rentPerHour;

    @PositiveOrZero(message = "Rent per day must be 0 or positive")
    private BigDecimal rentPerDay;

    @PositiveOrZero(message = "Monthly rent must be 0 or positive")
    private BigDecimal rentMonthly;

    /** Cloudinary image URLs after upload (max 3) */
    @JsonProperty("imageUrls")
    private java.util.List<String> imageUrls;
}
