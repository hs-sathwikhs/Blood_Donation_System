package com.blooddonation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Admin extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_id")
    private java.util.UUID adminId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}
