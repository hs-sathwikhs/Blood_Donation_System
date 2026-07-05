package com.blooddonation.controller;

import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.BloodStock;
import com.blooddonation.patterns.FIFOAllocationStrategy;
import com.blooddonation.patterns.PriorityAllocationStrategy;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.BloodStockRepository;
import com.blooddonation.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private BloodStockRepository bloodStockRepository;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @GetMapping("/stock")
    public ResponseEntity<Map<String, Double>> getAggregatedStock() {
        List<BloodStock> stocks = bloodStockRepository.findAll();
        Map<String, Double> stockByBloodType = new HashMap<>();

        for (BloodStock stock : stocks) {
            stockByBloodType.merge(stock.getBloodType(), stock.getQuantity(), Double::sum);
        }

        return ResponseEntity.ok(stockByBloodType);
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingRequests() {
        List<BloodRequest> pending = bloodRequestRepository.findAll().stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()) || "Created".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());

        // Return as plain maps to avoid lazy-loading/serialization issues
        List<Map<String, Object>> result = pending.stream().map(r -> {
            String severity = "Routine";
            if (r instanceof com.blooddonation.model.CriticalBloodRequest) severity = "Critical";
            else if (r instanceof com.blooddonation.model.UrgentBloodRequest) severity = "Urgent";

            return Map.<String, Object>of(
                "requestId", r.getRequestId().toString(),
                "bloodType", r.getBloodType(),
                "quantity", r.getQuantity(),
                "severity", severity,
                "status", r.getStatus()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/fulfill/{requestId}")
    public ResponseEntity<?> fulfillRequest(@PathVariable java.util.UUID requestId,
                                                 @RequestParam(defaultValue = "fifo") String strategyType) {
        try {
            boolean success = false;
            if ("priority".equalsIgnoreCase(strategyType)) {
                success = inventoryService.fulfillRequest(requestId, new PriorityAllocationStrategy());
            } else {
                success = inventoryService.fulfillRequest(requestId, new FIFOAllocationStrategy());
            }

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Request Fulfilled Successfully", "status", 200));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "Failed to fulfill request due to insufficient stock.", "error", "InsufficientStock"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "error", "Exception"));
        }
    }

    // ─── Stock Management Panel Endpoints ───

    @GetMapping("/stock/detailed")
    public ResponseEntity<List<Map<String, Object>>> getDetailedStock() {
        List<BloodStock> stocks = bloodStockRepository.findAll();
        List<Map<String, Object>> result = stocks.stream().map(s -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("stockId", s.getStockId().toString());
            m.put("bloodType", s.getBloodType());
            m.put("quantity", s.getQuantity());
            m.put("dateAdded", s.getDateAdded().toString());
            m.put("expiryDate", s.getExpiryDate().toString());
            m.put("isRare", s.isRare());
            m.put("expired", s.getExpiryDate().isBefore(java.time.LocalDate.now()));
            return m;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/stock/add")
    public ResponseEntity<?> addStock(@RequestBody Map<String, String> payload) {
        try {
            com.blooddonation.util.ValidationUtils.quantity(payload, 1, 1000);
            var inventory = inventoryService.getDefaultInventory();
            BloodStock stock = new BloodStock();
            stock.setBloodType(com.blooddonation.util.ValidationUtils.bloodType(payload));
            stock.setQuantity(com.blooddonation.util.ValidationUtils.quantity(payload, 0, 1000));
            stock.setDateAdded(java.time.LocalDate.now());
            stock.setExpiryDate(java.time.LocalDate.now().plusDays(42));
            stock.setInventory(inventory);
            stock.setRare("O-".equals(payload.get("bloodType")) || "AB-".equals(payload.get("bloodType")) || "B-".equals(payload.get("bloodType")));
            stock.setLowStockThreshold(10.0);
            bloodStockRepository.save(stock);
            return ResponseEntity.ok(Map.of("message", "Added " + payload.get("quantity") + " units of " + payload.get("bloodType")));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw ex;
        }
    }

    @PatchMapping("/stock/{stockId}")
    public ResponseEntity<?> updateStock(@PathVariable java.util.UUID stockId, @RequestBody Map<String, String> payload) {
        BloodStock stock = bloodStockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.setQuantity(com.blooddonation.util.ValidationUtils.quantity(payload, 0, 1000));
        bloodStockRepository.save(stock);
        return ResponseEntity.ok(Map.of("message", "Stock updated to " + payload.get("quantity") + " units"));
    }

    @DeleteMapping("/stock/expired")
    public ResponseEntity<?> removeExpiredStock() {
        List<BloodStock> all = bloodStockRepository.findAll();
        long removed = 0;
        for (BloodStock s : all) {
            if (s.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                bloodStockRepository.delete(s);
                removed++;
            }
        }
        return ResponseEntity.ok(Map.of("message", "Removed " + removed + " expired stock entries"));
    }
}
