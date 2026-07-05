package com.blooddonation.security;

import com.blooddonation.model.Camp;
import com.blooddonation.repository.CampRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.UUID;

@Component
public class Access {
    private final CampRepository campRepo;

    public Access(CampRepository campRepo) {
        this.campRepo = campRepo;
    }

    public UUID userId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    public void owner(UUID id) {
        if (!userId().equals(id)) throw new AccessDeniedException("This record belongs to another account");
    }

    public Camp camp(UUID id) {
        Camp camp = campRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Camp not found"));
        if (camp.getOrganiser() == null) throw new AccessDeniedException("Camp has no organiser");
        owner(camp.getOrganiser().getOrganiserId());
        return camp;
    }
}
