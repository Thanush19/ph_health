package com.backend.server.service;

import com.backend.server.dto.LikeResponse;
import com.backend.server.entity.SpaceLike;
import com.backend.server.repository.ParkingSpaceRepository;
import com.backend.server.repository.SpaceLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final SpaceLikeRepository spaceLikeRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public LikeResponse getLikeInfo(Long spaceId, String username) {
        if (!parkingSpaceRepository.existsById(spaceId)) {
            throw new IllegalArgumentException("Space not found: " + spaceId);
        }
        long count = spaceLikeRepository.countBySpaceId(spaceId);
        Long userId = userService.getUserIdByUsername(username);
        boolean likedByMe = spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId).isPresent();
        return LikeResponse.builder()
                .likeCount(count)
                .likedByMe(likedByMe)
                .build();
    }

    @Transactional
    public LikeResponse like(Long spaceId, String username) {
        if (!parkingSpaceRepository.existsById(spaceId)) {
            throw new IllegalArgumentException("Space not found: " + spaceId);
        }
        Long userId = userService.getUserIdByUsername(username);
        if (spaceLikeRepository.findBySpaceIdAndUserId(spaceId, userId).isPresent()) {
            return getLikeInfo(spaceId, username);
        }
        spaceLikeRepository.save(SpaceLike.builder()
                .spaceId(spaceId)
                .userId(userId)
                .build());
        return getLikeInfo(spaceId, username);
    }

    @Transactional
    public LikeResponse unlike(Long spaceId, String username) {
        if (!parkingSpaceRepository.existsById(spaceId)) {
            throw new IllegalArgumentException("Space not found: " + spaceId);
        }
        Long userId = userService.getUserIdByUsername(username);
        spaceLikeRepository.deleteBySpaceIdAndUserId(spaceId, userId);
        return getLikeInfo(spaceId, username);
    }
}
