package com.blooddonation.service;

import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.BloodStock;
import com.blooddonation.model.Donation;
import com.blooddonation.model.Inventory;
import com.blooddonation.patterns.AllocationStrategy;
import com.blooddonation.patterns.InventoryManager;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.BloodStockRepository;
import com.blooddonation.repository.InventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private BloodStockRepository bloodStockRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;
    
    @Autowired
    private InventoryManager inventoryManager;

    @PostConstruct
    public void init() {
        // Ensure default inventory exists for simplicity in this demo
        if (inventoryRepository.count() == 0) {
            Inventory inv = new Inventory();
            inv.setLocation("Central Blood Bank");
            inventoryRepository.save(inv);
        }
    }

    public void addDonationToInventory(Donation donation) {
        Inventory inv = inventoryRepository.findAll().get(0);
        
        BloodStock stock = new BloodStock();
        stock.setInventory(inv);
        stock.setBloodType(donation.getBloodType());
        stock.setQuantity(donation.getQuantity());
        stock.setDateAdded(LocalDate.now());
        stock.setExpiryDate(LocalDate.now().plusDays(42));
        stock.setLowStockThreshold(10.0);
        
        // Use singleton pattern method
        inventoryManager.addStock(stock);
    }
    
    public boolean fulfillRequest(java.util.UUID requestId, AllocationStrategy strategy) {
        BloodRequest request = bloodRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Request not found"));
            
        List<BloodStock> availableStocks = inventoryManager.getAvailableStock(request.getBloodType(), strategy);
        
        double requestedAmt = request.getQuantity();
        double fulfilledAmt = 0;
        
        for (BloodStock stock : availableStocks) {
            if (fulfilledAmt >= requestedAmt) break;
            
            double availableFromStock = stock.getQuantity();
            if (availableFromStock > 0) {
                double toTake = Math.min(availableFromStock, requestedAmt - fulfilledAmt);
                stock.setQuantity(availableFromStock - toTake);
                bloodStockRepository.save(stock);
                fulfilledAmt += toTake;
            }
        }
        
        if (fulfilledAmt >= requestedAmt) {
            request.setStatus("Fulfilled");
            bloodRequestRepository.save(request);
            return true;
        } else {
            // Not enough stock, rollback conceptually handled by not committing these partial takes as a failure
            // Real implementation might rollback the transaction here.
            throw new IllegalStateException("Insufficient stock to fulfill request");
        }
    }
    
    public void checkLowStockAndAlert() {
        // Simulated process that would run via @Scheduled daily
        // We'd check bloodStockRepository for types where SUM(quantity) < threshold
        System.out.println("Checking low stock alerts...");
    }

    public Inventory getDefaultInventory() {
        return inventoryRepository.findAll().get(0);
    }
}
