package com.backend.server.repository;

import com.backend.server.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    List<ParkingSpace> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
