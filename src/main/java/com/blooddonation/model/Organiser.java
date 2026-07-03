package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "organiser")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Organiser extends User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "organiser_id")
    private java.util.UUID organiserId;

    @Column(nullable = false)
    private String name;

    @Column(length = 15)
    private String contact;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "organiser", cascade = CascadeType.ALL)
    private List<Camp> camps;

    @Override
    public void login() {
        System.out.println("Organiser login logic");
    }

    @Override
    public void logout() {
        System.out.println("Organiser logout logic");
    }
}
