package com.blooddonation.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Urgent")
public class UrgentBloodRequest extends BloodRequest {
    
    @Override
    public int getComputedPriority() {
        return 2;
    }
}
