package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "blood_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BloodStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stock_id")
    private java.util.UUID stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "blood_type", length = 5, nullable = false)
    private String bloodType;

    @Column(nullable = false)
    private double quantity;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "is_rare")
    private boolean isRare = false;

    @Column(name = "low_stock_threshold", nullable = false)
    private double lowStockThreshold;

    @Column(name = "date_added", nullable = false)
    private LocalDate dateAdded;

    @Version
    private Long version;
}
