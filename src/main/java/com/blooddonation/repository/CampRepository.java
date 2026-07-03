package com.blooddonation.repository;

import com.blooddonation.model.Camp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampRepository extends JpaRepository<Camp, java.util.UUID> {
}
