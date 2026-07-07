package com.blooddonation.repository;

import com.blooddonation.model.Camp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface CampRepository extends JpaRepository<Camp, UUID> {
    org.springframework.data.domain.Page<Camp> findByOrganiserOrganiserId(UUID id, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Camp> findByStatusNot(String status, org.springframework.data.domain.Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Camp c where c.campId = :id")
    Optional<Camp> lockById(UUID id);
}
