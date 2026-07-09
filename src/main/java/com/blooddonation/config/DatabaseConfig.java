package com.blooddonation.config;

import com.blooddonation.model.*;
import com.blooddonation.patterns.BloodRequestFactory;
import com.blooddonation.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
@Profile("demo")
public class DatabaseConfig {

    @Bean
    CommandLineRunner initDatabase(OrganiserRepository organiserRepo,
                                   DonatorRepository donatorRepo,
                                   HospitalRepository hospitalRepo,
                                   AdminRepository adminRepo,
                                   InventoryRepository inventoryRepo,
                                   CampRepository campRepository,
                                   BloodStockRepository bloodStockRepository,
                                   BloodRequestRepository bloodRequestRepository,
                                   RegistrationRepository registrationRepository,
                                   org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {

            if (adminRepo.count() == 0) {
                Admin admin = new Admin();
                admin.setName("Blood Bank Admin");
                admin.setEmail("admin@bloodbank.org");
                admin.setPassword(passwordEncoder.encode("admin123"));
                adminRepo.save(admin);
            }

            Organiser org1 = null, org2 = null;
            if (organiserRepo.count() == 0) {
                org1 = new Organiser();
                org1.setName("Priya Sharma");
                org1.setEmail("priya.sharma@redcross.in");
                org1.setPassword(passwordEncoder.encode("password123"));
                org1.setContact("9876543210");
                org1 = organiserRepo.save(org1);

                org2 = new Organiser();
                org2.setName("Rahul Deshmukh");
                org2.setEmail("rahul.d@bloodaid.org");
                org2.setPassword(passwordEncoder.encode("password123"));
                org2.setContact("8765432109");
                org2 = organiserRepo.save(org2);
            } else {
                List<Organiser> orgs = organiserRepo.findAll();
                org1 = orgs.get(0);
                if (orgs.size() > 1) org2 = orgs.get(1);
            }

            Donator d1 = null, d2 = null, d3 = null, d4 = null, d5 = null;
            if (donatorRepo.count() == 0) {
                d1 = new Donator(); d1.setName("Aarav Patel"); d1.setAge(28); d1.setBloodType("O+");
                d1.setEmail("aarav.patel@gmail.com"); d1.setPassword(passwordEncoder.encode("pass123"));
                d1.setContact("9001234567"); d1.setLastDonationDate(LocalDate.now().minusDays(120));
                d1.setEligible(true); d1 = donatorRepo.save(d1);

                d2 = new Donator(); d2.setName("Sneha Reddy"); d2.setAge(24); d2.setBloodType("A+");
                d2.setEmail("sneha.reddy@gmail.com"); d2.setPassword(passwordEncoder.encode("pass123"));
                d2.setContact("9112345678"); d2.setLastDonationDate(LocalDate.now().minusDays(200));
                d2.setEligible(true); d2 = donatorRepo.save(d2);

                d3 = new Donator(); d3.setName("Vikram Singh"); d3.setAge(35); d3.setBloodType("B-");
                d3.setEmail("vikram.singh@yahoo.com"); d3.setPassword(passwordEncoder.encode("pass123"));
                d3.setContact("9223456789"); d3.setEligible(true); d3 = donatorRepo.save(d3);

                d4 = new Donator(); d4.setName("Meera Iyer"); d4.setAge(22); d4.setBloodType("AB+");
                d4.setEmail("meera.iyer@outlook.com"); d4.setPassword(passwordEncoder.encode("pass123"));
                d4.setContact("9334567890"); d4.setLastDonationDate(LocalDate.now().minusDays(30));
                d4.setEligible(false); d4 = donatorRepo.save(d4);

                d5 = new Donator(); d5.setName("Arjun Nair"); d5.setAge(40); d5.setBloodType("O-");
                d5.setEmail("arjun.nair@gmail.com"); d5.setPassword(passwordEncoder.encode("pass123"));
                d5.setContact("9445678901"); d5.setLastDonationDate(LocalDate.now().minusDays(95));
                d5.setEligible(true); d5 = donatorRepo.save(d5);
            } else {
                List<Donator> donors = donatorRepo.findAll();
                d1 = donors.get(0);
            }

            Hospital h1 = null, h2 = null, h3 = null, h4 = null, h5 = null;
            if (hospitalRepo.count() == 0) {
                h1 = new Hospital(); h1.setName("Manipal Hospital"); h1.setLocation("Bengaluru");
                h1.setRegistrationNo("KA-BLR-001"); h1.setEmail("blood@manipal-blr.org");
                h1.setPassword(passwordEncoder.encode("hosp123")); h1.setContact("0802345678");
                h1 = hospitalRepo.save(h1);

                h2 = new Hospital(); h2.setName("Apollo Hospital"); h2.setLocation("Hyderabad");
                h2.setRegistrationNo("TS-HYD-002"); h2.setEmail("blood@apollo-hyd.org");
                h2.setPassword(passwordEncoder.encode("hosp123")); h2.setContact("0401234567");
                h2 = hospitalRepo.save(h2);

                h3 = new Hospital(); h3.setName("Lilavati Hospital"); h3.setLocation("Mumbai");
                h3.setRegistrationNo("MH-MUM-003"); h3.setEmail("blood@lilavati-mum.org");
                h3.setPassword(passwordEncoder.encode("hosp123")); h3.setContact("0222345678");
                h3 = hospitalRepo.save(h3);

                h4 = new Hospital(); h4.setName("Fortis Hospital"); h4.setLocation("Bengaluru");
                h4.setRegistrationNo("KA-BLR-004"); h4.setEmail("blood@fortis-blr.org");
                h4.setPassword(passwordEncoder.encode("hosp123")); h4.setContact("0803456789");
                h4 = hospitalRepo.save(h4);

                h5 = new Hospital(); h5.setName("KIMS Hospital"); h5.setLocation("Hyderabad");
                h5.setRegistrationNo("TS-HYD-005"); h5.setEmail("blood@kims-hyd.org");
                h5.setPassword(passwordEncoder.encode("hosp123")); h5.setContact("0402345678");
                h5 = hospitalRepo.save(h5);
            } else {
                List<Hospital> hospitals = hospitalRepo.findAll();
                h1 = hospitals.get(0);
                if (hospitals.size() > 1) h2 = hospitals.get(1);
                if (hospitals.size() > 2) h3 = hospitals.get(2);
                if (hospitals.size() > 3) h4 = hospitals.get(3);
                if (hospitals.size() > 4) h5 = hospitals.get(4);
            }

            Camp c1 = null, c2 = null, c3 = null;
            if (campRepository.count() == 0) {

                c1 = new Camp(); c1.setName("Indiranagar Blood Drive"); c1.setLocation("Bengaluru - Indiranagar");
                c1.setDate(LocalDate.now()); c1.setTime(LocalTime.of(9, 0));
                c1.setCapacity(60); c1.setStatus("Active"); c1.setOrganiser(org1);
                c1 = campRepository.save(c1);

                c2 = new Camp(); c2.setName("Jubilee Hills Health Camp"); c2.setLocation("Hyderabad - Jubilee Hills");
                c2.setDate(LocalDate.now()); c2.setTime(LocalTime.of(10, 0));
                c2.setCapacity(80); c2.setStatus("Active"); c2.setOrganiser(org2);
                c2 = campRepository.save(c2);

                c3 = new Camp(); c3.setName("Andheri Community Drive"); c3.setLocation("Mumbai - Andheri West");
                c3.setDate(LocalDate.now().plusDays(15)); c3.setTime(LocalTime.of(8, 30));
                c3.setCapacity(100); c3.setStatus("Scheduled"); c3.setOrganiser(org1);
                c3 = campRepository.save(c3);

                Camp c4 = new Camp(); c4.setName("Koramangala Tech Park Drive"); c4.setLocation("Bengaluru - Koramangala");
                c4.setDate(LocalDate.now().plusDays(20)); c4.setTime(LocalTime.of(11, 0));
                c4.setCapacity(40); c4.setStatus("Draft"); c4.setOrganiser(org1);
                campRepository.save(c4);

                Camp c5 = new Camp(); c5.setName("Banjara Hills Camp"); c5.setLocation("Hyderabad - Banjara Hills");
                c5.setDate(LocalDate.now().plusDays(3)); c5.setTime(LocalTime.of(9, 30));
                c5.setCapacity(50); c5.setStatus("Cancelled"); c5.setOrganiser(org2);
                campRepository.save(c5);

                Camp c6 = new Camp(); c6.setName("Dadar Blood Donation Mela"); c6.setLocation("Mumbai - Dadar");
                c6.setDate(LocalDate.now().minusDays(10)); c6.setTime(LocalTime.of(10, 0));
                c6.setCapacity(70); c6.setStatus("Completed"); c6.setOrganiser(org2);
                campRepository.save(c6);
            } else {
                List<Camp> camps = campRepository.findAll();
                c1 = camps.get(0);
                if (camps.size() > 1) c2 = camps.get(1);
                if (camps.size() > 2) c3 = camps.get(2);
            }

            if (registrationRepository.count() == 0 && d1 != null && c1 != null) {
                Registration r1 = new Registration(); r1.setDonor(d1); r1.setCamp(c1);
                r1.setRegistrationDate(LocalDate.now().minusDays(2)); r1.setStatus("Registered");
                registrationRepository.save(r1);

                if (d2 != null) {
                    Registration r2 = new Registration(); r2.setDonor(d2); r2.setCamp(c1);
                    r2.setRegistrationDate(LocalDate.now().minusDays(1)); r2.setStatus("Registered");
                    registrationRepository.save(r2);
                }
                if (d3 != null && c2 != null) {
                    Registration r3 = new Registration(); r3.setDonor(d3); r3.setCamp(c2);
                    r3.setRegistrationDate(LocalDate.now()); r3.setStatus("Registered");
                    registrationRepository.save(r3);
                }
                if (d5 != null && c2 != null) {
                    Registration r4 = new Registration(); r4.setDonor(d5); r4.setCamp(c2);
                    r4.setRegistrationDate(LocalDate.now()); r4.setStatus("Registered");
                    registrationRepository.save(r4);
                }
            }

            Inventory inventory = inventoryRepo.findAll().isEmpty() ? null : inventoryRepo.findAll().get(0);
            if (inventory == null) {
                inventory = new Inventory();
                inventory.setLocation("Central Blood Bank");
                inventory = inventoryRepo.save(inventory);
            }

            if (bloodStockRepository.count() == 0) {
                String[] types = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
                double[] qtys  = {42.0, 14.0, 31.0, 9.0,  58.0, 6.0,  18.0,  4.0};
                boolean[] rare = {false, false, false, true, false, true, false, true};
                int[] daysAgo  = {2, 6, 3, 9, 1, 12, 4, 15};

                for (int i = 0; i < types.length; i++) {
                    BloodStock stock = new BloodStock();
                    stock.setBloodType(types[i]);
                    stock.setQuantity(qtys[i]);
                    stock.setDateAdded(LocalDate.now().minusDays(daysAgo[i]));
                    stock.setExpiryDate(LocalDate.now().plusDays(42 - daysAgo[i]));
                    stock.setInventory(inventory);
                    stock.setRare(rare[i]);
                    stock.setLowStockThreshold(10.0);
                    bloodStockRepository.save(stock);
                }
            }

            if (bloodRequestRepository.count() == 0) {

                BloodRequest req1 = BloodRequestFactory.createRequest(h1, "O-", 3.0, "Critical");
                req1.setStatus("Pending"); req1.setRequestDate(LocalDate.now());
                bloodRequestRepository.save(req1);

                BloodRequest req2 = BloodRequestFactory.createRequest(h2, "A+", 8.0, "Urgent");
                req2.setStatus("Pending"); req2.setRequestDate(LocalDate.now().minusDays(1));
                bloodRequestRepository.save(req2);

                BloodRequest req3 = BloodRequestFactory.createRequest(h3, "B+", 12.0, "Routine");
                req3.setStatus("Pending"); req3.setRequestDate(LocalDate.now().minusDays(2));
                bloodRequestRepository.save(req3);

                BloodRequest req4 = BloodRequestFactory.createRequest(h4, "AB-", 2.0, "Critical");
                req4.setStatus("Pending"); req4.setRequestDate(LocalDate.now());
                bloodRequestRepository.save(req4);

                BloodRequest req5 = BloodRequestFactory.createRequest(h5, "O+", 15.0, "Urgent");
                req5.setStatus("Pending"); req5.setRequestDate(LocalDate.now().minusDays(1));
                bloodRequestRepository.save(req5);

                BloodRequest req6 = BloodRequestFactory.createRequest(h2, "B-", 4.0, "Routine");
                req6.setStatus("Fulfilled"); req6.setRequestDate(LocalDate.now().minusDays(5));
                bloodRequestRepository.save(req6);

                BloodRequest req7 = BloodRequestFactory.createRequest(h1, "A+", 5.0, "Urgent");
                req7.setStatus("Fulfilled"); req7.setRequestDate(LocalDate.now().minusDays(3));
                bloodRequestRepository.save(req7);

                BloodRequest req8 = BloodRequestFactory.createRequest(h3, "O-", 1.0, "Critical");
                req8.setStatus("Pending"); req8.setRequestDate(LocalDate.now());
                bloodRequestRepository.save(req8);
            }
        };
    }
}
