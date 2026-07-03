package com.blooddonation.util;

import java.util.Arrays;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public static boolean isValidBloodType(String bloodType) {
        return bloodType != null && Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                     .contains(bloodType);
    }
    
    public static boolean isEligibleAge(int age) {
        return age >= 18 && age <= 65;
    }
}
