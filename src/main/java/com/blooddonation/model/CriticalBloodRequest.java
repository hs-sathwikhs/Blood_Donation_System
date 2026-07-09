package com.blooddonation.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Critical")
public class CriticalBloodRequest extends BloodRequest {
    
    @Override
    public int getComputedPriority() {
        return 1;
    }
}
