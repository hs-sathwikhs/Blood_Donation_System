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
        stock.setRare(java.util.Set.of("O-", "AB-", "B-").contains(donation.getBloodType()));

        inventoryManager.addStock(stock);
    }

    public boolean fulfillRequest(java.util.UUID requestId, AllocationStrategy strategy) {
        BloodRequest request = bloodRequestRepository.lockById(requestId)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Request not found"));

        if (!"Pending".equals(request.getStatus())) throw new IllegalStateException("This request has already been processed");
        List<BloodStock> availableStocks = inventoryManager.getAvailableStock(request.getBloodType(), strategy);

        double requestedAmt = request.getQuantity();
        if (availableStocks.stream().mapToDouble(BloodStock::getQuantity).sum() < requestedAmt) {
            throw new IllegalStateException("Insufficient usable stock");
        }
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

        request.setStatus("Fulfilled");
        bloodRequestRepository.save(request);
        return true;
    }

    public void updateStock(java.util.UUID id, double qty) {
        BloodStock stock = bloodStockRepository.lockById(id).orElseThrow(() ->
                new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Stock not found"));
        stock.setQuantity(qty);
    }

    public long removeExpiredStock() {
        return bloodStockRepository.deleteByExpiryDateBefore(LocalDate.now());
    }

    public Inventory getDefaultInventory() {
        return inventoryRepository.findAll().get(0);
    }
}
