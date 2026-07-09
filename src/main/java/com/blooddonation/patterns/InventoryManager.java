package com.blooddonation.patterns;

import com.blooddonation.model.BloodStock;
import com.blooddonation.repository.BloodStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InventoryManager {

    @Autowired
    private BloodStockRepository bloodStockRepository;

    public void addStock(BloodStock stock) {
        bloodStockRepository.save(stock);
    }
    
    public List<BloodStock> getAvailableStock(String bloodType, AllocationStrategy strategy) {
        return strategy.allocate(bloodType, LocalDate.now(), bloodStockRepository);
    }
}
