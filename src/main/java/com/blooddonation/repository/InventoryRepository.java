package com.blooddonation.repository;

import com.blooddonation.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, java.util.UUID> {
}
