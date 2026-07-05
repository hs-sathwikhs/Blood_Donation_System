package com.blooddonation.controller;

import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import com.blooddonation.security.JwtUtils;
import com.blooddonation.util.ValidationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final OrganiserRepository organiserRepo;
    private final DonatorRepository donorRepo;
    private final HospitalRepository hospitalRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(OrganiserRepository organiserRepo, DonatorRepository donorRepo,
            HospitalRepository hospitalRepo, AdminRepository adminRepo, PasswordEncoder encoder, JwtUtils jwtUtils) {
        this.organiserRepo = organiserRepo;
        this.donorRepo = donorRepo;
        this.hospitalRepo = hospitalRepo;
        this.adminRepo = adminRepo;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login/{role}")
    public ResponseEntity<?> login(@PathVariable String role, @RequestBody Map<String, String> data) {
        String email = ValidationUtils.email(data);
        String password = ValidationUtils.password(data, false);
        User usr = switch (role) {
            case "donor" -> donorRepo.findByEmail(email).orElse(null);
            case "organiser" -> organiserRepo.findByEmail(email).orElse(null);
            case "hospital" -> hospitalRepo.findByEmail(email).orElse(null);
            case "admin" -> adminRepo.findByEmail(email).orElse(null);
            default -> throw new IllegalArgumentException("Choose a valid role");
        };
        if (usr == null || !encoder.matches(password, usr.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Email or password is incorrect"));
        }
        UUID id = usr instanceof Donator d ? d.getDonatorId() : usr instanceof Organiser o ? o.getOrganiserId()
                : usr instanceof Hospital h ? h.getHospitalId() : ((Admin) usr).getAdminId();
        String token = jwtUtils.generateToken(id.toString(), "ROLE_" + role.toUpperCase(java.util.Locale.ROOT));
        return ResponseEntity.ok(Map.of("token", token, "id", id, "name", usr.getName(), "role", role));
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<?> register(@PathVariable String role, @RequestBody Map<String, String> data) {
        String email = ValidationUtils.email(data);
        String name = ValidationUtils.text(data, "name", 100);
        String password = ValidationUtils.password(data, true);
        String contact = data.getOrDefault("contact", "").trim();
        if (!contact.matches("[+0-9 ()-]{0,15}")) throw new IllegalArgumentException("Enter a valid contact number");
        if (donorRepo.findByEmail(email).isPresent() || organiserRepo.findByEmail(email).isPresent()
                || hospitalRepo.findByEmail(email).isPresent() || adminRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        User usr;
        switch (role) {
            case "donor" -> {
                Donator donor = new Donator();
                int age = Integer.parseInt(ValidationUtils.text(data, "age", 3));
                if (age < 18 || age > 65) throw new IllegalArgumentException("Donors must be between 18 and 65");
                donor.setAge(age); donor.setBloodType(ValidationUtils.bloodType(data));
                donor.setContact(contact); usr = donor;
            }
            case "organiser" -> {
                Organiser org = new Organiser(); org.setContact(contact); usr = org;
            }
            case "hospital" -> {
                Hospital hospital = new Hospital();
                hospital.setLocation(ValidationUtils.text(data, "location", 200));
                hospital.setRegistrationNo(ValidationUtils.text(data, "registrationNo", 50));
                hospital.setContact(contact); usr = hospital;
            }
            default -> throw new IllegalArgumentException("Choose a valid role");
        }
        usr.setName(name); usr.setEmail(email); usr.setPassword(encoder.encode(password));
        if (usr instanceof Donator d) donorRepo.save(d);
        else if (usr instanceof Organiser o) organiserRepo.save(o);
        else hospitalRepo.save((Hospital) usr);
        return ResponseEntity.status(201).body(Map.of("message", "Account created. You can sign in now"));
    }
}
