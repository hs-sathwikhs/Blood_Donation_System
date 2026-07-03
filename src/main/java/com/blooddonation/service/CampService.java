package com.blooddonation.service;

import com.blooddonation.dto.CampDTO;
import com.blooddonation.model.Camp;
import com.blooddonation.model.Registration;
import com.blooddonation.patterns.NotificationService;
import com.blooddonation.repository.CampRepository;
import com.blooddonation.repository.DonatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private DonatorRepository donatorRepository;

    @Autowired
    @Qualifier("emailServiceAdapter")
    private NotificationService notificationService;

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
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));

        camp.setStatus("Scheduled");
        camp = campRepository.save(camp);

        // Fire async event (Observer pattern via Spring Events)
        eventPublisher.publishEvent(new com.blooddonation.patterns.CampPublishedEvent(this, camp));

        return camp;
    }

    public Camp updateCamp(java.util.UUID campId, CampDTO dto) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));

        if ("Cancelled".equals(camp.getStatus())) {
            throw new IllegalStateException("Cannot update a cancelled camp");
        }
        if (dto.getName() != null) camp.setName(dto.getName());
        if (dto.getLocation() != null) camp.setLocation(dto.getLocation());
        if (dto.getDate() != null) camp.setDate(dto.getDate());
        if (dto.getTime() != null) camp.setTime(dto.getTime());
        if (dto.getCapacity() > 0) camp.setCapacity(dto.getCapacity());

        return campRepository.save(camp);
    }

    public void cancelCamp(java.util.UUID campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));
        camp.setStatus("Cancelled");
        campRepository.save(camp);
    }

    /**
     * Camp lifecycle: Draft → Scheduled → Active → Completed
     * Any non-completed state can also → Cancelled
     */
    public Camp changeStatus(java.util.UUID campId, String newStatus) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));

        String current = camp.getStatus();

        // Validate transition
        boolean valid = switch (newStatus) {
            case "Scheduled" -> "Draft".equals(current);
            case "Active"    -> "Scheduled".equals(current) || "Draft".equals(current);
            case "Completed" -> "Active".equals(current);
            case "Cancelled" -> !"Completed".equals(current) && !"Cancelled".equals(current);
            default -> false;
        };

        if (!valid) {
            throw new IllegalStateException("Cannot transition from '" + current + "' to '" + newStatus + "'");
        }

        camp.setStatus(newStatus);
        camp = campRepository.save(camp);

        // If published/scheduled, fire observer notification
        if ("Scheduled".equals(newStatus)) {
            eventPublisher.publishEvent(new com.blooddonation.patterns.CampPublishedEvent(this, camp));
        }

        return camp;
    }

    public List<Registration> getCampRegistrations(java.util.UUID campId) {
        Camp camp = campRepository.findById(campId)
                .orElseThrow(() -> new RuntimeException("Camp not found"));
        return camp.getRegistrations();
    }

    public Page<Camp> getAllCamps(Pageable pageable) {
        return campRepository.findAll(pageable);
    }

    public Camp getCampById(java.util.UUID id) {
        return campRepository.findById(id).orElse(null);
    }
}
