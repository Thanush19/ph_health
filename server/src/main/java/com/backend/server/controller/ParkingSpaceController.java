package com.backend.server.controller;

import com.backend.server.dto.CreateSpaceRequest;
import com.backend.server.dto.ImageUploadResponse;
import com.backend.server.dto.SpaceResponse;
import com.backend.server.service.ParkingSpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        ImageUploadResponse response = parkingSpaceService.uploadImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SpaceResponse> createSpace(@Valid @RequestBody CreateSpaceRequest request) {
        String username = getCurrentUsername();
        SpaceResponse response = parkingSpaceService.createSpace(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return auth.getName();
    }
}
