package com.backend.server.repository;

import com.backend.server.entity.SpaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {

    long countBySpaceId(Long spaceId);

    Optional<SpaceLike> findBySpaceIdAndUserId(Long spaceId, Long userId);

    void deleteBySpaceIdAndUserId(Long spaceId, Long userId);
}
