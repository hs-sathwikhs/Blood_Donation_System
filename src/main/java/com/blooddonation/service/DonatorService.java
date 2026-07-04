package com.blooddonation.service;

import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import com.blooddonation.util.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class DonatorService {

    @Autowired
    private DonatorRepository donatorRepository;

    @Autowired
    private CampRepository campRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private InventoryService inventoryService;

    // EligibilityCriteria from class diagram - encapsulates validation rules
    private final EligibilityCriteria eligibilityCriteria = new EligibilityCriteria();

    public Donator registerDonator(Donator donator) {
        if (!ValidationUtils.isValidEmail(donator.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!eligibilityCriteria.validateDonor(donator)) {
            donator.setEligible(false);
        }
        return donatorRepository.save(donator);
    }

    public boolean checkEligibility(java.util.UUID donorId) {
        Donator donator = donatorRepository.findById(donorId)
            .orElseThrow(() -> new RuntimeException("Donor not found"));
        return eligibilityCriteria.validateDonor(donator);
    }

    public Registration registerForCamp(java.util.UUID donorId, java.util.UUID campId) {
        if (!checkEligibility(donorId)) {
            throw new IllegalStateException("Donor is not eligible for donation currently");
        }

        Camp camp = campRepository.findById(campId)
            .orElseThrow(() -> new RuntimeException("Camp not found"));

        if (camp.getRegistrations().size() >= camp.getCapacity()) {
            throw new IllegalStateException("Camp capacity reached");
        }

        Donator donator = donatorRepository.findById(donorId).get();

        Registration reg = new Registration();
        reg.setCamp(camp);
        reg.setDonor(donator);
        reg.setRegistrationDate(LocalDate.now());
        reg.setStatus("Registered");

        return registrationRepository.save(reg);
    }

    public Donation recordDonation(java.util.UUID donorId, java.util.UUID campId, double quantity) {
        Donator donator = donatorRepository.findById(donorId)
            .orElseThrow(() -> new RuntimeException("Donor not found"));
        Camp camp = campRepository.findById(campId).orElse(null);

        Donation donation = new Donation();
        donation.setDonor(donator);
        donation.setCamp(camp);
        donation.setDonationDate(LocalDate.now());
        donation.setQuantity(quantity);
        donation.setBloodType(donator.getBloodType());
        donation.setStatus("Completed");

        // Update donor's last donation date
        donator.setLastDonationDate(LocalDate.now());
        donatorRepository.save(donator);

        // Add to inventory
        inventoryService.addDonationToInventory(donation);

        return donationRepository.save(donation);
    }
}
