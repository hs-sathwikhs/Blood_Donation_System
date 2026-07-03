package com.blooddonation.repository;

import com.blooddonation.model.Donator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonatorRepository extends JpaRepository<Donator, java.util.UUID> {
    Optional<Donator> findByEmail(String email);
}
