package com.blooddonation.controller;

import com.blooddonation.dto.CampDTO;
import com.blooddonation.model.Camp;
import com.blooddonation.model.Registration;
import com.blooddonation.service.CampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/camps")
public class CampController {

    @Autowired
    private CampService campService;

    @Autowired
    private com.blooddonation.security.Access access;

    @Autowired
    private com.blooddonation.repository.OrganiserRepository organiserRepo;

    @PostMapping
    public ResponseEntity<CampDTO> createCamp(@jakarta.validation.Valid @RequestBody CampDTO campDTO) {
        Camp camp = new Camp();
        camp.setName(campDTO.getName());
        camp.setLocation(campDTO.getLocation());
        camp.setDate(campDTO.getDate());
        camp.setTime(campDTO.getTime());
        camp.setCapacity(campDTO.getCapacity());
        camp.setOrganiser(organiserRepo.findById(access.userId()).orElseThrow());

        Camp savedCamp = campService.createCamp(camp);

        campDTO.setCampId(savedCamp.getCampId());
        campDTO.setStatus(savedCamp.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(campDTO);
    }

    @GetMapping
    public ResponseEntity<Page<CampDTO>> getAllCamps(Pageable pageable) {
        Page<CampDTO> camps = campService.getAllCamps(pageable).map(this::toDTO);
        return ResponseEntity.ok(camps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampDTO> getCampById(@PathVariable java.util.UUID id) {
        Camp camp = campService.getCampById(id);
        if (camp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toDTO(camp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampDTO> updateCamp(@PathVariable java.util.UUID id,
                                               @jakarta.validation.Valid @RequestBody CampDTO campDTO) {
        access.camp(id);
        Camp updated = campService.updateCamp(id, campDTO);
        return ResponseEntity.ok(toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelCamp(@PathVariable java.util.UUID id) {
        access.camp(id);
        campService.cancelCamp(id);
        return ResponseEntity.ok(Map.of("message", "Camp cancelled successfully"));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<CampDTO> publishCamp(@PathVariable java.util.UUID id) {
        access.camp(id);
        Camp camp = campService.publishCamp(id);
        return ResponseEntity.ok(toDTO(camp));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable java.util.UUID id,
                                           @RequestBody Map<String, String> payload) {
        access.camp(id);
        String newStatus = payload.get("status");
        Camp camp = campService.changeStatus(id, newStatus);
        return ResponseEntity.ok(toDTO(camp));
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<Map<String, Object>>> getCampRegistrations(@PathVariable java.util.UUID id) {
        access.camp(id);
        List<Registration> registrations = campService.getCampRegistrations(id);
        List<Map<String, Object>> result = registrations.stream().map(r -> Map.<String, Object>of(
                "registrationId", r.getRegistrationId(),
                "donorName", r.getDonor().getName(),
                "bloodType", r.getDonor().getBloodType(),
                "registrationDate", r.getRegistrationDate().toString(),
                "status", r.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private CampDTO toDTO(Camp c) {
        CampDTO dto = new CampDTO();
        dto.setCampId(c.getCampId());
        dto.setName(c.getName());
        dto.setLocation(c.getLocation());
        dto.setDate(c.getDate());
        dto.setTime(c.getTime());
        dto.setCapacity(c.getCapacity());
        dto.setStatus(c.getStatus());
        return dto;
    }
}
