package com.blooddonation.repository;

import com.blooddonation.model.BloodStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BloodStockRepository extends JpaRepository<BloodStock, java.util.UUID> {
    List<BloodStock> findByBloodTypeAndExpiryDateAfterOrderByDateAddedAsc(String bloodType, LocalDate date);
    List<BloodStock> findByBloodTypeAndExpiryDateAfterOrderByExpiryDateAsc(String bloodType, LocalDate date);
}
