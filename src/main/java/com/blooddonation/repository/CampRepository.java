package com.blooddonation.repository;

import com.blooddonation.model.Camp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface CampRepository extends JpaRepository<Camp, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Camp c where c.campId = :id")
    Optional<Camp> lockById(UUID id);
}
