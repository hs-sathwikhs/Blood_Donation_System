package com.blooddonation.util;

import java.util.Map;
import java.util.Set;

public class ValidationUtils {
    private static final Set<String> bloodTypes = Set.of("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-");

    public static String text(Map<String, String> data, String key, int max) {
        String value = data.get(key);
        if (value == null || value.isBlank() || value.trim().length() > max) {
            throw new IllegalArgumentException("Enter a valid " + key);
        }
        return value.trim();
    }

    public static String email(Map<String, String> data) {
        String email = text(data, "email", 254).toLowerCase(java.util.Locale.ROOT);
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }
        return email;
    }

    public static String password(Map<String, String> data, boolean registering) {
        String password = data.get("password");
        if (password == null || password.length() < (registering ? 8 : 1)
                || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must be " + (registering ? "8 to 72 bytes" : "1 to 72 bytes"));
        }
        return password;
    }

    public static double quantity(Map<String, String> data, double min, double max) {
        try {
            double qty = Double.parseDouble(text(data, "quantity", 20));
            if (Double.isFinite(qty) && qty >= min && qty <= max) return qty;
        } catch (NumberFormatException ex) {
            // return the same message for malformed numbers
        }
        throw new IllegalArgumentException("Quantity must be between " + min + " and " + max);
    }

    public static String bloodType(Map<String, String> data) {
        String type = text(data, "bloodType", 3);
        if (!bloodTypes.contains(type)) throw new IllegalArgumentException("Choose a valid blood group");
        return type;
    }
}
