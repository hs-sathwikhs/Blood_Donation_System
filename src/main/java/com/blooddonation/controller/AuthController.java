package com.blooddonation.controller;

import com.blooddonation.model.*;
import com.blooddonation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private OrganiserRepository organiserRepo;

    @Autowired
    private DonatorRepository donatorRepo;

    @Autowired
    private HospitalRepository hospitalRepo;

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.blooddonation.security.JwtUtils jwtUtils;

    // ─── LOGIN ENDPOINTS ───

    @PostMapping("/login/organiser")
    public ResponseEntity<?> loginOrganiser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String pswd = credentials.get("password");
        Optional<Organiser> opt = organiserRepo.findByEmail(email);
        if (opt.isPresent() && passwordEncoder.matches(pswd, opt.get().getPassword())) {
            String token = jwtUtils.generateToken(email, "ROLE_ORGANISER");
            return ResponseEntity.ok(Map.of("token", token, "id", opt.get().getOrganiserId(), "name", opt.get().getName()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/login/donor")
    public ResponseEntity<?> loginDonor(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String pswd = credentials.get("password");
        Optional<Donator> opt = donatorRepo.findByEmail(email);
        if (opt.isPresent() && passwordEncoder.matches(pswd, opt.get().getPassword())) {
            String token = jwtUtils.generateToken(email, "ROLE_DONOR");
            return ResponseEntity.ok(Map.of("token", token, "id", opt.get().getDonatorId(), "name", opt.get().getName()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/login/hospital")
    public ResponseEntity<?> loginHospital(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String pswd = credentials.get("password");
        Optional<Hospital> opt = hospitalRepo.findByEmail(email);
        if (opt.isPresent() && passwordEncoder.matches(pswd, opt.get().getPassword())) {
            String token = jwtUtils.generateToken(email, "ROLE_HOSPITAL");
            return ResponseEntity.ok(Map.of("token", token, "id", opt.get().getHospitalId(), "name", opt.get().getName()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String pswd = credentials.get("password");
        Optional<Admin> opt = adminRepo.findByEmail(email);
        if (opt.isPresent() && passwordEncoder.matches(pswd, opt.get().getPassword())) {
            String token = jwtUtils.generateToken(email, "ROLE_ADMIN");
            return ResponseEntity.ok(Map.of("token", token, "id", opt.get().getAdminId(), "name", opt.get().getName()));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    // ─── REGISTRATION ENDPOINTS ───

    @PostMapping("/register/donor")
    public ResponseEntity<?> registerDonor(@RequestBody Map<String, String> payload) {
        try {
            if (donatorRepo.findByEmail(payload.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
            }
            Donator d = new Donator();
            d.setName(payload.get("name"));
            d.setAge(Integer.parseInt(payload.get("age")));
            d.setBloodType(payload.get("bloodType"));
            d.setEmail(payload.get("email"));
            d.setPassword(passwordEncoder.encode(payload.get("password")));
            d.setContact(payload.getOrDefault("contact", ""));
            d.setEligible(true);
            donatorRepo.save(d);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Donor registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register/hospital")
    public ResponseEntity<?> registerHospital(@RequestBody Map<String, String> payload) {
        try {
            if (hospitalRepo.findByEmail(payload.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
            }
            Hospital h = new Hospital();
            h.setName(payload.get("name"));
            h.setLocation(payload.get("location"));
            h.setRegistrationNo(payload.get("registrationNo"));
            h.setEmail(payload.get("email"));
            h.setPassword(passwordEncoder.encode(payload.get("password")));
            h.setContact(payload.getOrDefault("contact", ""));
            hospitalRepo.save(h);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Hospital registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register/organiser")
    public ResponseEntity<?> registerOrganiser(@RequestBody Map<String, String> payload) {
        try {
            if (organiserRepo.findByEmail(payload.get("email")).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
            }
            Organiser o = new Organiser();
            o.setName(payload.get("name"));
            o.setEmail(payload.get("email"));
            o.setPassword(passwordEncoder.encode(payload.get("password")));
            o.setContact(payload.getOrDefault("contact", ""));
            organiserRepo.save(o);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Organiser registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
