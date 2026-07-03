package com.blooddonation.controller;

import com.blooddonation.model.Donation;
import com.blooddonation.model.Donator;
import com.blooddonation.model.Registration;
import com.blooddonation.repository.DonationRepository;
import com.blooddonation.repository.DonatorRepository;
import com.blooddonation.service.DonatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donors")
public class DonatorController {

    @Autowired
    private DonatorService donatorService;

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    private DonationRepository donationRepository;

    @GetMapping("/{id}/donations")
    public ResponseEntity<List<Map<String, Object>>> getDonationHistory(@PathVariable java.util.UUID id) {
        // Use DonationRepository directly to avoid lazy-loading serialization issues
        List<Donation> donations = donationRepository.findAll().stream()
                .filter(d -> d.getDonor().getDonatorId().equals(id))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = donations.stream().map(d -> Map.<String, Object>of(
                "donationId", d.getDonationId().toString(),
                "bloodType", d.getBloodType(),
                "quantity", d.getQuantity(),
                "donationDate", d.getDonationDate().toString(),
                "status", d.getStatus()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable java.util.UUID id) {
        boolean eligible = donatorService.checkEligibility(id);
        return ResponseEntity.ok(eligible);
    }

    @PostMapping("/register-camp")
    public ResponseEntity<String> registerForCamp(@RequestBody Map<String, String> payload) {
        try {
            java.util.UUID donorId = java.util.UUID.fromString(payload.get("donorId"));
            java.util.UUID campId = java.util.UUID.fromString(payload.get("campId"));
            Registration reg = donatorService.registerForCamp(donorId, campId);
            return ResponseEntity.ok("Successfully registered with ID: " + reg.getRegistrationId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/donate")
    public ResponseEntity<String> recordDonation(@RequestBody Map<String, String> payload) {
        try {
            java.util.UUID donorId = java.util.UUID.fromString(payload.get("donorId"));
            java.util.UUID campId = java.util.UUID.fromString(payload.get("campId"));
            double quantity = Double.parseDouble(payload.get("quantity"));
            Donation donation = donatorService.recordDonation(donorId, campId, quantity);
            return ResponseEntity.ok("Donation recorded: " + donation.getDonationId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
