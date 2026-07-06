package com.blooddonation.patterns;

import com.blooddonation.model.BloodRequest;
import com.blooddonation.model.CriticalBloodRequest;
import com.blooddonation.model.Hospital;
import com.blooddonation.model.RoutineBloodRequest;
import com.blooddonation.model.UrgentBloodRequest;

import java.time.LocalDate;

public class BloodRequestFactory {

    public static BloodRequest createRequest(Hospital hospital, String bloodType, double quantity, String severity) {
        BloodRequest request;

        switch (severity == null ? "" : severity.toLowerCase(java.util.Locale.ROOT)) {
            case "critical":
                request = new CriticalBloodRequest();
                break;
            case "urgent":
                request = new UrgentBloodRequest();
                break;
            case "routine":
                request = new RoutineBloodRequest();
                break;
            default:
                throw new IllegalArgumentException("Invalid severity: " + severity);
        }

        request.setHospital(hospital);
        request.setBloodType(bloodType);
        request.setQuantity(quantity);
        request.setRequestDate(LocalDate.now());
        request.setStatus("Pending");
        request.setPriority(request.getComputedPriority());

        return request;
    }
}
