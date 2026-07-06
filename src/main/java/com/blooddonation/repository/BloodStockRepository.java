package com.blooddonation.repository;

import com.blooddonation.model.BloodStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodStockRepository extends JpaRepository<BloodStock, java.util.UUID> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select s from BloodStock s where s.stockId = :id")
    java.util.Optional<BloodStock> lockById(java.util.UUID id);

    long deleteByExpiryDateBefore(LocalDate date);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    List<BloodStock> findByBloodTypeAndExpiryDateGreaterThanEqualOrderByDateAddedAsc(String bloodType, LocalDate date);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    List<BloodStock> findByBloodTypeAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(String bloodType, LocalDate date);
}
