package com.backend.server.service;

import com.backend.server.dto.CreateSpaceRequest;
import com.backend.server.dto.ImageUploadResponse;
import com.backend.server.dto.SpaceResponse;
import com.backend.server.entity.ParkingSpace;
import com.backend.server.repository.ParkingSpaceRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final Cloudinary cloudinary;
    private final UserService userService;

    public ImageUploadResponse uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        File tempFile = File.createTempFile("upload_", "_" + (file.getOriginalFilename() != null ? file.getOriginalFilename() : "image"));
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            out.write(file.getBytes());
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());
            String url = (String) result.get("secure_url");
            if (url == null) {
                url = (String) result.get("url");
            }
            return new ImageUploadResponse(url);
        } finally {
            tempFile.delete();
        }
    }

    public SpaceResponse createSpace(String ownerUsername, CreateSpaceRequest request) {
        validateAtLeastOneRentOption(request.getRentPerHour(), request.getRentPerDay(), request.getRentMonthly());

        String imageUrlsStr = request.getImageUrls() != null && !request.getImageUrls().isEmpty()
                ? String.join(",", request.getImageUrls())
                : null;
        ParkingSpace space = ParkingSpace.builder()
                .ownerId(userService.getUserIdByUsername(ownerUsername))
                .address(request.getAddress())
                .squareFeet(request.getSquareFeet())
                .vehicleTypes(request.getVehicleTypes())
                .rentPerHour(request.getRentPerHour())
                .rentPerDay(request.getRentPerDay())
                .rentMonthly(request.getRentMonthly())
                .imageUrls(imageUrlsStr)
                .build();

        space = parkingSpaceRepository.save(space);
        return toResponse(space);
    }

    public List<SpaceResponse> listAllSpaces() {
        return parkingSpaceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateAtLeastOneRentOption(BigDecimal perHour, BigDecimal perDay, BigDecimal monthly) {
        boolean hasAny = (perHour != null && perHour.compareTo(BigDecimal.ZERO) > 0)
                || (perDay != null && perDay.compareTo(BigDecimal.ZERO) > 0)
                || (monthly != null && monthly.compareTo(BigDecimal.ZERO) > 0);
        if (!hasAny) {
            throw new IllegalArgumentException("At least one rent option (per hour, per day, or monthly) must be set and positive");
        }
    }

    private SpaceResponse toResponse(ParkingSpace space) {
        List<String> imageUrlsList = space.getImageUrls() != null && !space.getImageUrls().isEmpty()
                ? List.of(space.getImageUrls().split(","))
                : List.of();
        return SpaceResponse.builder()
                .id(space.getId())
                .ownerId(space.getOwnerId())
                .address(space.getAddress())
                .squareFeet(space.getSquareFeet())
                .vehicleTypes(space.getVehicleTypes())
                .rentPerHour(space.getRentPerHour())
                .rentPerDay(space.getRentPerDay())
                .rentMonthly(space.getRentMonthly())
                .imageUrls(imageUrlsList)
                .createdAt(space.getCreatedAt())
                .build();
    }
}
