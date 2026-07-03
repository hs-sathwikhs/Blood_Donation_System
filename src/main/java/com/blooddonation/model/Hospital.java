package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospital")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Hospital extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hospital_id")
    private java.util.UUID hospitalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(length = 15)
    private String contact;

    @Column(name = "registration_no", nullable = false, unique = true)
    private String registrationNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<BloodRequest> bloodRequests = new ArrayList<>();

    @Override
    public void login() {
        System.out.println("Hospital login logic");
    }

    @Override
    public void logout() {
        System.out.println("Hospital logout logic");
    }
}
