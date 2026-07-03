package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Camp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "camp_id")
    private java.util.UUID campId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organiser_id")
    private Organiser organiser;

    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL)
    private List<Donation> donations = new ArrayList<>();

}
