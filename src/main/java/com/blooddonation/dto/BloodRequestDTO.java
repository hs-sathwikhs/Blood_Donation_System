package com.blooddonation.dto;

import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class BloodRequestDTO {
    private java.util.UUID requestId;
    
    @NotNull(message = "Hospital ID is required")
    private java.util.UUID hospitalId;
    
    @NotBlank(message = "Blood Type is required")
    private String bloodType;
    
    @Min(value = 1, message = "Quantity must be at least 1 unit")
    private double quantity;
    
    @NotBlank(message = "Severity is required")
    private String severity;
    
    private String status;
    private LocalDate requestDate;
}
