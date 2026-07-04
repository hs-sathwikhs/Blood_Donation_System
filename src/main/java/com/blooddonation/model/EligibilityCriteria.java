package com.blooddonation.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * EligibilityCriteria - validates donor eligibility based on configurable rules.
 * From Class Diagram: minAge, maxAge, minDaysSinceLastDonation, validateDonor()
 */
public class EligibilityCriteria {

    private int minAge;
    private int maxAge;
    private int minDaysSinceLastDonation;

    public EligibilityCriteria() {
        this.minAge = 18;
        this.maxAge = 65;
        this.minDaysSinceLastDonation = 90;
    }

    public EligibilityCriteria(int minAge, int maxAge, int minDaysSinceLastDonation) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.minDaysSinceLastDonation = minDaysSinceLastDonation;
    }

    public boolean validateDonor(Donator donor) {
        if (donor.getAge() < minAge || donor.getAge() > maxAge) {
            return false;
        }
        if (donor.getLastDonationDate() != null) {
            long days = ChronoUnit.DAYS.between(donor.getLastDonationDate(), LocalDate.now());
            return days >= minDaysSinceLastDonation;
        }
        return true;
    }

    // Getters
    public int getMinAge() { return minAge; }
    public int getMaxAge() { return maxAge; }
    public int getMinDaysSinceLastDonation() { return minDaysSinceLastDonation; }
}
