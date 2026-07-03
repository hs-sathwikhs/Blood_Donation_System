package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "donation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Donation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "donation_id")
    private java.util.UUID donationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donator donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_id")
    private Camp camp;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate;

    @Column(nullable = false)
    private double quantity;

    @Column(name = "blood_type", length = 5, nullable = false)
    private String bloodType;

    @Column(nullable = false)
    private String status;
}
