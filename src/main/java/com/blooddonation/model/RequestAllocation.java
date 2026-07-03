package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "request_allocation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RequestAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "allocation_id")
    private java.util.UUID allocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private BloodRequest bloodRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private BloodStock bloodStock;

    @Column(name = "units_allocated", nullable = false)
    private double unitsAllocated;
}
