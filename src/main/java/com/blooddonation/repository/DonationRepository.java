package com.blooddonation.repository;

import com.blooddonation.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, java.util.UUID> {
}
