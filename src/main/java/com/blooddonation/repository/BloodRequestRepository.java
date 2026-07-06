package com.blooddonation.repository;

import com.blooddonation.model.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, java.util.UUID> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from BloodRequest r where r.requestId = :id")
    java.util.Optional<BloodRequest> lockById(java.util.UUID id);
}
