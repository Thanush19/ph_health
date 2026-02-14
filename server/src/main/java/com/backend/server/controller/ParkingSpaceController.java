package com.backend.server.controller;

import com.backend.server.dto.CreateSpaceRequest;
import com.backend.server.dto.ImageUploadResponse;
import com.backend.server.dto.LikeResponse;
import com.backend.server.dto.SpaceResponse;
import com.backend.server.service.LikeService;
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
import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;
    private final LikeService likeService;

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        ImageUploadResponse response = parkingSpaceService.uploadImage(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SpaceResponse>> listSpaces() {
        return ResponseEntity.ok(parkingSpaceService.listAllSpaces());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<SpaceResponse>> listMySpaces() {
        return ResponseEntity.ok(parkingSpaceService.listMySpaces(getCurrentUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceResponse> getSpace(@PathVariable Long id) {
        return ResponseEntity.ok(parkingSpaceService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SpaceResponse> createSpace(@Valid @RequestBody CreateSpaceRequest request) {
        String username = getCurrentUsername();
        SpaceResponse response = parkingSpaceService.createSpace(username, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceResponse> updateSpace(
            @PathVariable Long id,
            @Valid @RequestBody CreateSpaceRequest request
    ) {
        SpaceResponse response = parkingSpaceService.updateSpace(id, getCurrentUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/like")
    public ResponseEntity<LikeResponse> getLike(@PathVariable Long id) {
        return ResponseEntity.ok(likeService.getLikeInfo(id, getCurrentUsername()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> like(@PathVariable Long id) {
        return ResponseEntity.ok(likeService.like(id, getCurrentUsername()));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<LikeResponse> unlike(@PathVariable Long id) {
        return ResponseEntity.ok(likeService.unlike(id, getCurrentUsername()));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return auth.getName();
    }
}
