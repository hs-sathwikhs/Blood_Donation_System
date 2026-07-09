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
    private com.blooddonation.security.Access access;

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private com.blooddonation.repository.RegistrationRepository regRepo;

    @GetMapping("/{id}/registrations")
    public List<Map<String, Object>> getRegistrations(@PathVariable java.util.UUID id) {
        access.owner(id);
        return regRepo.findByDonorDonatorId(id).stream().map(r -> Map.<String, Object>of(
                "campId", r.getCamp().getCampId(), "name", r.getCamp().getName(),
                "location", r.getCamp().getLocation(), "date", r.getCamp().getDate(),
                "campStatus", r.getCamp().getStatus(), "status", r.getStatus())).toList();
    }

    @DeleteMapping("/{id}/registrations/{campId}")
    public Map<String, String> cancelRegistration(@PathVariable java.util.UUID id, @PathVariable java.util.UUID campId) {
        access.owner(id);
        donatorService.cancelRegistration(id, campId);
        return Map.of("message", "Registration cancelled");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getProfile(@PathVariable java.util.UUID id) {
        access.owner(id);
        Donator d = donatorRepository.findById(id).orElseThrow();
        var res = new java.util.LinkedHashMap<String, Object>();
        res.put("name", d.getName()); res.put("bloodType", d.getBloodType());
        res.put("eligible", donatorService.checkEligibility(id));
        res.put("lastDonationDate", d.getLastDonationDate());
        res.put("nextDonationDate", d.getLastDonationDate() == null ? java.time.LocalDate.now() : d.getLastDonationDate().plusDays(90));
        return res;
    }

    @GetMapping("/{id}/donations")
    public ResponseEntity<List<Map<String, Object>>> getDonationHistory(@PathVariable java.util.UUID id) {
        access.owner(id);
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
        access.owner(id);
        boolean eligible = donatorService.checkEligibility(id);
        return ResponseEntity.ok(eligible);
    }

    @PostMapping("/register-camp")
    public ResponseEntity<String> registerForCamp(@RequestBody Map<String, String> payload) {
        java.util.UUID donorId = java.util.UUID.fromString(payload.get("donorId"));
        access.owner(donorId);
        java.util.UUID campId = java.util.UUID.fromString(payload.get("campId"));
        Registration reg = donatorService.registerForCamp(donorId, campId);
        return ResponseEntity.ok("Successfully registered with ID: " + reg.getRegistrationId());
    }

    @PostMapping("/donate")
    public ResponseEntity<String> recordDonation(@RequestBody Map<String, String> payload) {
        java.util.UUID donorId = java.util.UUID.fromString(payload.get("donorId"));
        access.owner(donorId);
        java.util.UUID campId = java.util.UUID.fromString(payload.get("campId"));
        double quantity = com.blooddonation.util.ValidationUtils.quantity(payload, 0.5, 1);
        Donation donation = donatorService.recordDonation(donorId, campId, quantity);
        return ResponseEntity.ok("Donation recorded: " + donation.getDonationId());
    }
}
