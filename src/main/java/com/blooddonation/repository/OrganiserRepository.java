package com.blooddonation.repository;

import com.blooddonation.model.Organiser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganiserRepository extends JpaRepository<Organiser, java.util.UUID> {
    Optional<Organiser> findByEmail(String email);
}
