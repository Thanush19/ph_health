package com.backend.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_spaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String address;

    @NotNull
    @Column(name = "square_feet", nullable = false)
    private Integer squareFeet;

    @NotBlank
    @Column(name = "vehicle_types", nullable = false, length = 500)
    private String vehicleTypes; // e.g. "Car, Bike, SUV" or JSON

    @Column(name = "rent_per_hour", precision = 10, scale = 2)
    private BigDecimal rentPerHour;

    @Column(name = "rent_per_day", precision = 10, scale = 2)
    private BigDecimal rentPerDay;

    @Column(name = "rent_monthly", precision = 10, scale = 2)
    private BigDecimal rentMonthly;

    /** Comma-separated image URLs (max 3). Uses image_url column for backward compatibility. */
    @Column(name = "image_url", length = 2000)
    private String imageUrls;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
