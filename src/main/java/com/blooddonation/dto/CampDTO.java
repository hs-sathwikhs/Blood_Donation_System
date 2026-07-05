package com.blooddonation.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class CampDTO {
    private java.util.UUID campId;
    
    @NotBlank(message = "Name cannot be empty")
    @jakarta.validation.constraints.Size(max = 100)
    private String name;
    
    @NotBlank(message = "Location cannot be empty")
    @jakarta.validation.constraints.Size(max = 200)
    private String location;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Time is required")
    private LocalTime time;
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
    
    private String status;
}
