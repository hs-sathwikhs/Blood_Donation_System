package com.blooddonation.repository;

import com.blooddonation.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    Optional<Registration> findByDonorDonatorIdAndCampCampId(UUID donorId, UUID campId);
    long countByCampCampIdAndStatusNot(UUID campId, String status);
    List<Registration> findByDonorDonatorId(UUID donorId);
}
