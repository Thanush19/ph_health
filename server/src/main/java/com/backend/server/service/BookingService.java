package com.backend.server.service;

import com.backend.server.dto.BookingResponse;
import com.backend.server.dto.CreateBookingRequest;
import com.backend.server.dto.DummyPaymentResponse;
import com.backend.server.entity.Booking;
import com.backend.server.entity.ParkingSpace;
import com.backend.server.repository.BookingRepository;
import com.backend.server.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PAID = "PAID";

    private final BookingRepository bookingRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserService userService;

    @Transactional
    public BookingResponse createBooking(Long spaceId, String username, CreateBookingRequest request) {
        ParkingSpace space = parkingSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Space not found: " + spaceId));
        Long userId = userService.getUserIdByUsername(username);

        if (request.getSlotStart().isAfter(request.getSlotEnd())) {
            throw new IllegalArgumentException("Slot start must be before slot end");
        }

        BigDecimal ratePerHour = space.getRentPerHour() != null && space.getRentPerHour().compareTo(BigDecimal.ZERO) > 0
                ? space.getRentPerHour()
                : (space.getRentPerDay() != null ? space.getRentPerDay().divide(BigDecimal.valueOf(24), 2, RoundingMode.HALF_UP) : BigDecimal.valueOf(10));
        long hours = ChronoUnit.HOURS.between(request.getSlotStart(), request.getSlotEnd());
        if (hours < 1) hours = 1;
        BigDecimal totalAmount = ratePerHour.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);

        Booking booking = Booking.builder()
                .spaceId(spaceId)
                .userId(userId)
                .slotStart(request.getSlotStart())
                .slotEnd(request.getSlotEnd())
                .totalAmount(totalAmount)
                .paymentStatus(STATUS_PENDING)
                .build();
        booking = bookingRepository.save(booking);
        return toResponse(booking);
    }

    @Transactional
    public DummyPaymentResponse processDummyPayment(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        Long userId = userService.getUserIdByUsername(username);
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not allowed to pay for this booking");
        }
        if (STATUS_PAID.equals(booking.getPaymentStatus())) {
            return DummyPaymentResponse.builder()
                    .success(true)
                    .transactionId("already-paid-" + bookingId)
                    .message("Already paid")
                    .build();
        }
        String txId = "DUMMY-" + UUID.randomUUID();
        booking.setPaymentStatus(STATUS_PAID);
        bookingRepository.save(booking);
        return DummyPaymentResponse.builder()
                .success(true)
                .transactionId(txId)
                .message("Payment successful (dummy)")
                .build();
    }

    private BookingResponse toResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .spaceId(b.getSpaceId())
                .userId(b.getUserId())
                .slotStart(b.getSlotStart())
                .slotEnd(b.getSlotEnd())
                .totalAmount(b.getTotalAmount())
                .paymentStatus(b.getPaymentStatus())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
