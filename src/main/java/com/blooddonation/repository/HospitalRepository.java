package com.blooddonation.repository;

import com.blooddonation.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, java.util.UUID> {
    Optional<Hospital> findByEmail(String email);
}
