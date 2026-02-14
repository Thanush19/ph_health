package com.backend.server.repository;

import com.backend.server.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findBySpaceIdAndRenterId(Long spaceId, Long renterId);
}
