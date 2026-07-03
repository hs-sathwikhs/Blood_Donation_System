package com.blooddonation.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Routine")
public class RoutineBloodRequest extends BloodRequest {
    
    @Override
    public int getComputedPriority() {
        return 3;
    }

    @Override
    public String getProcessingMessage() {
        return "ROUTINE: Process in standard time";
    }
}
