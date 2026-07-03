package com.blooddonation.patterns;

import com.blooddonation.model.BloodStock;
import com.blooddonation.repository.BloodStockRepository;

import java.time.LocalDate;
import java.util.List;

public interface AllocationStrategy {
    List<BloodStock> allocate(String bloodType, LocalDate currentDate, BloodStockRepository repository);
}
