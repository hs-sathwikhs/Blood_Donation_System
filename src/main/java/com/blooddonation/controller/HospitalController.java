package com.blooddonation.controller;

import com.blooddonation.dto.BloodRequestDTO;
import com.blooddonation.model.BloodRequest;
import com.blooddonation.service.HospitalService;
import com.blooddonation.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private com.blooddonation.security.Access access;

    @Autowired
    private BloodRequestRepository bloodRequestRepository;

    @PostMapping("/{hospitalId}/requests")
    public ResponseEntity<BloodRequestDTO> createBloodRequest(
            @PathVariable java.util.UUID hospitalId,
            @RequestBody Map<String, String> payload) {

        access.owner(hospitalId);
        String bloodType = com.blooddonation.util.ValidationUtils.bloodType(payload);
        double quantity = com.blooddonation.util.ValidationUtils.quantity(payload, 1, 1000);
        String severity = payload.get("severity");

        BloodRequest req = hospitalService.createBloodRequest(hospitalId, bloodType, quantity, severity);

        BloodRequestDTO dto = new BloodRequestDTO();
        dto.setRequestId(req.getRequestId());
        dto.setHospitalId(hospitalId);
        dto.setBloodType(req.getBloodType());
        dto.setQuantity(req.getQuantity());
        dto.setSeverity(severity);
        dto.setStatus(req.getStatus());
        dto.setRequestDate(req.getRequestDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{hospitalId}/requests")
    public ResponseEntity<List<Map<String, Object>>> getRequests(@PathVariable java.util.UUID hospitalId) {
        access.owner(hospitalId);
        List<BloodRequest> requests = bloodRequestRepository.findAll().stream()
                .filter(r -> r.getHospital().getHospitalId().equals(hospitalId))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = requests.stream().map(r -> {
            String severity = "Routine";
            if (r instanceof com.blooddonation.model.CriticalBloodRequest) severity = "Critical";
            else if (r instanceof com.blooddonation.model.UrgentBloodRequest) severity = "Urgent";

            return Map.<String, Object>of(
                "requestId", r.getRequestId().toString(),
                "bloodType", r.getBloodType(),
                "quantity", r.getQuantity(),
                "severity", severity,
                "status", r.getStatus(),
                "requestDate", r.getRequestDate().toString()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
