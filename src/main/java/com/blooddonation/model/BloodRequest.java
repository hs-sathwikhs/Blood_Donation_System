package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "blood_request")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "severity", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class BloodRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "request_id")
    protected java.util.UUID requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    protected Hospital hospital;

    @Column(name = "blood_type", length = 5, nullable = false)
    protected String bloodType;

    @Column(nullable = false)
    protected double quantity;

    @Column(name = "request_date", nullable = false)
    protected LocalDate requestDate;

    @Column(nullable = false)
    protected String status;

    @Column(nullable = false)
    protected int priority;
    
    @Column(name = "severity", insertable = false, updatable = false)
    protected String severity;

    public abstract int getComputedPriority();
    public abstract String getProcessingMessage();
}
