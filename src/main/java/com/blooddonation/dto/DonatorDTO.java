package com.blooddonation.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class DonatorDTO {
    private java.util.UUID donorId;
    
    @NotBlank(message = "Name cannot be empty")
    private String name;
    
    @Min(value = 18, message = "Age must be at least 18")
    private int age;
    
    @NotBlank(message = "Blood type cannot be empty")
    private String bloodType;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;
}
