package com.blooddonation.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
}
