package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donator")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Donator extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "donator_id")
    private java.util.UUID donatorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int age;

    @Column(name = "blood_type", length = 5, nullable = false)
    private String bloodType;

    @Column(length = 15)
    private String contact;

    @Column(name = "last_donation_date")
    private LocalDate lastDonationDate;

    @Column(name = "is_eligible")
    private boolean isEligible = true;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<Donation> donations = new ArrayList<>();

    @Override
    public void login() {
        System.out.println("Donator login logic");
    }

    @Override
    public void logout() {
        System.out.println("Donator logout logic");
    }
}
