package com.blooddonation.repository;

import com.blooddonation.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, java.util.UUID> {
}
