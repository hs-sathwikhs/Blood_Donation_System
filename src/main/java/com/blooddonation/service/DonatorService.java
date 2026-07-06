package com.blooddonation.service;

import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class DonatorService {
    private final DonatorRepository donatorRepository;
    private final CampRepository campRepo;
    private final RegistrationRepository regRepo;
    private final DonationRepository donationRepo;
    private final InventoryService inventoryService;
    private final EligibilityCriteria criteria = new EligibilityCriteria();

    public DonatorService(DonatorRepository donatorRepository, CampRepository campRepo,
            RegistrationRepository regRepo, DonationRepository donationRepo, InventoryService inventoryService) {
        this.donatorRepository = donatorRepository;
        this.campRepo = campRepo;
        this.regRepo = regRepo;
        this.donationRepo = donationRepo;
        this.inventoryService = inventoryService;
    }

    public boolean checkEligibility(UUID donorId) {
        return criteria.validateDonor(donor(donorId));
    }

    public Registration registerForCamp(UUID donorId, UUID campId) {
        Camp camp = campRepo.lockById(campId).orElseThrow(() -> missing("Camp"));
        Donator donor = donor(donorId);
        if (!criteria.validateDonor(donor)) throw new IllegalStateException("You are not eligible to donate yet");
        if ((!"Scheduled".equals(camp.getStatus()) && !"Active".equals(camp.getStatus()))
                || camp.getDate().isBefore(LocalDate.now())) throw new IllegalStateException("This camp is not accepting registrations");
        Registration reg = regRepo.findByDonorDonatorIdAndCampCampId(donorId, campId).orElse(new Registration());
        if (reg.getRegistrationId() != null && !"Cancelled".equals(reg.getStatus())) {
            throw new IllegalStateException("You have already registered for this camp");
        }
        if (regRepo.countByCampCampIdAndStatusNot(campId, "Cancelled") >= camp.getCapacity()) {
            throw new IllegalStateException("This camp is full");
        }
        reg.setDonor(donor); reg.setCamp(camp);
        reg.setRegistrationDate(LocalDate.now()); reg.setStatus("Registered");
        return regRepo.save(reg);
    }

    public Donation recordDonation(UUID donorId, UUID campId, double qty) {
        // lock the donor to enforce the waiting period on concurrent submissions
        Donator donor = donatorRepository.lockById(donorId).orElseThrow(() -> missing("Donor"));
        Camp camp = campRepo.lockById(campId).orElseThrow(() -> missing("Camp"));
        if (!Double.isFinite(qty) || qty < 0.5 || qty > 1) throw new IllegalArgumentException("Donation must be 0.5 to 1 unit");
        if (!criteria.validateDonor(donor)) throw new IllegalStateException("You are not eligible to donate yet");
        if (!"Active".equals(camp.getStatus()) || !camp.getDate().equals(LocalDate.now())) {
            throw new IllegalStateException("Donations can be recorded on the day of an active camp");
        }
        Registration reg = regRepo.findByDonorDonatorIdAndCampCampId(donorId, campId)
                .orElseThrow(() -> new IllegalStateException("Register for this camp first"));
        if (!"Registered".equals(reg.getStatus())) throw new IllegalStateException("This registration is not active");
        Donation donation = new Donation();
        donation.setDonor(donor); donation.setCamp(camp); donation.setQuantity(qty);
        donation.setBloodType(donor.getBloodType()); donation.setDonationDate(LocalDate.now());
        donation.setStatus("Completed");
        donor.setLastDonationDate(LocalDate.now()); donor.setEligible(false);
        reg.setStatus("Donated");
        inventoryService.addDonationToInventory(donation);
        return donationRepo.save(donation);
    }

    private Donator donor(UUID id) {
        return donatorRepository.findById(id).orElseThrow(() -> missing("Donor"));
    }

    private ResponseStatusException missing(String name) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, name + " not found");
    }
}
