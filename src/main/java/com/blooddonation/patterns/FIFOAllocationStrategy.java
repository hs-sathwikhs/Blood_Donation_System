package com.blooddonation.patterns;

import com.blooddonation.model.BloodStock;
import com.blooddonation.repository.BloodStockRepository;

import java.time.LocalDate;
import java.util.List;

public class FIFOAllocationStrategy implements AllocationStrategy {

    @Override
    public List<BloodStock> allocate(String bloodType, LocalDate currentDate, BloodStockRepository repository) {
        return repository.findByBloodTypeAndExpiryDateGreaterThanEqualOrderByDateAddedAsc(bloodType, currentDate);
    }
}
