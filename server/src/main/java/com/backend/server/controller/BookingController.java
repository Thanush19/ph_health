package com.backend.server.controller;

import com.backend.server.dto.BookingResponse;
import com.backend.server.dto.CreateBookingRequest;
import com.backend.server.dto.DummyPaymentResponse;
import com.backend.server.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/spaces/{spaceId}/book")
    public ResponseEntity<BookingResponse> createBooking(
            @PathVariable Long spaceId,
            @Valid @RequestBody CreateBookingRequest request
    ) {
        BookingResponse response = bookingService.createBooking(spaceId, getCurrentUsername(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bookings/{bookingId}/pay-dummy")
    public ResponseEntity<DummyPaymentResponse> dummyPayment(@PathVariable Long bookingId) {
        DummyPaymentResponse response = bookingService.processDummyPayment(bookingId, getCurrentUsername());
        return ResponseEntity.ok(response);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return auth.getName();
    }
}
