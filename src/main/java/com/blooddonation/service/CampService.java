package com.blooddonation.service;

import com.blooddonation.dto.CampDTO;
import com.blooddonation.model.Camp;
import com.blooddonation.model.Registration;
import com.blooddonation.repository.CampRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CampService {

    @Autowired
    private CampRepository campRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Camp createCamp(Camp camp) {
        if (camp.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Camp date cannot be in the past");
        }
        camp.setStatus("Draft");
        return campRepository.save(camp);
    }

    public Camp publishCamp(java.util.UUID campId) {
        return changeStatus(campId, "Scheduled");
    }

    public Camp updateCamp(java.util.UUID campId, CampDTO dto) {
        Camp camp = campRepository.lockById(campId)
                .orElseThrow(() -> missing());

        if ("Cancelled".equals(camp.getStatus()) || "Completed".equals(camp.getStatus())) {
            throw new IllegalStateException("Cannot update a cancelled or completed camp");
        }
        if ("Active".equals(camp.getStatus()) && !dto.getDate().equals(camp.getDate()))
            throw new IllegalStateException("Cannot change the date of an active camp");
        if (dto.getDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Camp date cannot be in the past");
        long places = camp.getRegistrations().stream().filter(r -> !"Cancelled".equals(r.getStatus())).count();
        if (dto.getCapacity() < places) throw new IllegalStateException("Capacity cannot be less than registered donors");
        if (dto.getName() != null) camp.setName(dto.getName());
        if (dto.getLocation() != null) camp.setLocation(dto.getLocation());
        if (dto.getDate() != null) camp.setDate(dto.getDate());
        if (dto.getTime() != null) camp.setTime(dto.getTime());
        if (dto.getCapacity() > 0) camp.setCapacity(dto.getCapacity());

        return campRepository.save(camp);
    }

    public void cancelCamp(java.util.UUID campId) {
        changeStatus(campId, "Cancelled");
    }

    public Camp changeStatus(java.util.UUID campId, String newStatus) {
        Camp camp = campRepository.lockById(campId)
                .orElseThrow(() -> missing());

        String current = camp.getStatus();

        boolean valid = switch (newStatus == null ? "" : newStatus) {
            case "Scheduled" -> "Draft".equals(current);
            case "Active"    -> "Scheduled".equals(current) && camp.getDate().equals(LocalDate.now());
            case "Completed" -> "Active".equals(current);
            case "Cancelled" -> !"Completed".equals(current) && !"Cancelled".equals(current);
            default -> false;
        };

        if (!valid) {
            throw new IllegalStateException("Cannot transition from '" + current + "' to '" + newStatus + "'");
        }

        camp.setStatus(newStatus);
        if ("Cancelled".equals(newStatus)) camp.getRegistrations().stream()
                .filter(r -> "Registered".equals(r.getStatus())).forEach(r -> r.setStatus("Cancelled"));
        camp = campRepository.save(camp);

        if ("Scheduled".equals(newStatus)) {
            eventPublisher.publishEvent(new com.blooddonation.patterns.CampPublishedEvent(this, camp));
        }

        return camp;
    }

    public List<Registration> getCampRegistrations(java.util.UUID campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> missing());
        return camp.getRegistrations();
    }

    public Page<Camp> getAllCamps(Pageable pageable) {
        return campRepository.findAll(pageable);
    }

    public Camp getCampById(java.util.UUID id) {
        return campRepository.findById(id).orElse(null);
    }

    private org.springframework.web.server.ResponseStatusException missing() {
        return new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Camp not found");
    }
}
