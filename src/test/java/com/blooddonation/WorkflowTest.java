package com.blooddonation;

import com.blooddonation.model.*;
import com.blooddonation.patterns.*;
import com.blooddonation.repository.*;
import com.blooddonation.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:workflowTests;DB_CLOSE_DELAY=-1")
class WorkflowTest {
    @Autowired DonatorService donors;
    @Autowired CampService camps;
    @Autowired InventoryService inventory;
    @Autowired DonatorRepository donorRepo;
    @Autowired CampRepository campRepo;
    @Autowired RegistrationRepository regRepo;
    @Autowired DonationRepository donationRepo;
    @Autowired HospitalRepository hospitalRepo;
    @Autowired BloodRequestRepository requestRepo;
    @Autowired BloodStockRepository stockRepo;
    @Autowired org.springframework.transaction.PlatformTransactionManager txManager;

    Donator donor() {
        Donator d = new Donator();
        d.setName("Test donor"); d.setEmail(UUID.randomUUID() + "@example.org");
        d.setPassword("test hash"); d.setAge(23); d.setBloodType("AB+");
        return donorRepo.save(d);
    }

    Camp camp(int capacity) {
        Camp c = new Camp(); c.setName("Test camp"); c.setLocation("Bengaluru");
        c.setDate(LocalDate.now()); c.setTime(LocalTime.of(9, 0));
        c.setCapacity(capacity); c.setStatus("Active");
        return campRepo.save(c);
    }

    @Test
    void donationRequiresRegistrationAndUpdatesAllRecordsOnce() {
        Donator d = donor(); Camp c = camp(3);
        assertThrows(IllegalStateException.class, () -> donors.recordDonation(d.getDonatorId(), c.getCampId(), 1));
        donors.registerForCamp(d.getDonatorId(), c.getCampId());
        assertThrows(IllegalStateException.class, () -> donors.registerForCamp(d.getDonatorId(), c.getCampId()));
        assertThrows(IllegalArgumentException.class, () -> donors.recordDonation(d.getDonatorId(), c.getCampId(), -1));
        long count = donationRepo.count();
        double qty = stockRepo.findAll().stream().mapToDouble(BloodStock::getQuantity).sum();
        donors.recordDonation(d.getDonatorId(), c.getCampId(), 1);
        assertEquals(count + 1, donationRepo.count());
        assertEquals(qty + 1, stockRepo.findAll().stream().mapToDouble(BloodStock::getQuantity).sum());
        assertEquals("Donated", regRepo.findByDonorDonatorIdAndCampCampId(d.getDonatorId(), c.getCampId()).orElseThrow().getStatus());
        assertFalse(donors.checkEligibility(d.getDonatorId()));
        assertThrows(IllegalStateException.class, () -> donors.recordDonation(d.getDonatorId(), c.getCampId(), 1));
        assertEquals(count + 1, donationRepo.count());
    }

    @Test
    void cancelledAndPastCampsRejectRegistration() {
        Donator d = donor(); Camp c = camp(3);
        c.setDate(LocalDate.now().minusDays(1)); campRepo.save(c);
        assertThrows(IllegalStateException.class, () -> donors.registerForCamp(d.getDonatorId(), c.getCampId()));
        c.setDate(LocalDate.now()); c.setStatus("Cancelled"); campRepo.save(c);
        assertThrows(IllegalStateException.class, () -> donors.registerForCamp(d.getDonatorId(), c.getCampId()));
        c.setStatus("Draft"); campRepo.save(c);
        assertThrows(IllegalStateException.class, () -> camps.changeStatus(c.getCampId(), "Active"));
    }

    @Test
    void cancellingRegistrationReleasesItsPlace() {
        Donator a = donor(); Donator b = donor(); Camp c = camp(1);
        donors.registerForCamp(a.getDonatorId(), c.getCampId());
        assertThrows(IllegalStateException.class, () -> donors.registerForCamp(b.getDonatorId(), c.getCampId()));
        donors.cancelRegistration(a.getDonatorId(), c.getCampId());
        donors.registerForCamp(b.getDonatorId(), c.getCampId());
        assertEquals(1, regRepo.countByCampCampIdAndStatusNot(c.getCampId(), "Cancelled"));
        camps.changeStatus(c.getCampId(), "Cancelled");
        assertEquals("Cancelled", regRepo.findByDonorDonatorIdAndCampCampId(b.getDonatorId(), c.getCampId()).orElseThrow().getStatus());
    }

    @Test
    void concurrentRegistrationCannotOverbook() throws Exception {
        Camp c = camp(1); Donator a = donor(); Donator b = donor();
        var pool = Executors.newFixedThreadPool(2);
        try {
            var jobs = pool.invokeAll(java.util.List.of(
                () -> register(a, c), () -> register(b, c)));
            assertEquals(1, jobs.stream().filter(j -> { try { return (Boolean) j.get(); } catch (Exception ex) { throw new RuntimeException(ex); } }).count());
            assertEquals(1, regRepo.countByCampCampIdAndStatusNot(c.getCampId(), "Cancelled"));
        } finally { pool.shutdownNow(); }
    }

    boolean register(Donator d, Camp c) {
        try { donors.registerForCamp(d.getDonatorId(), c.getCampId()); return true; }
        catch (IllegalStateException ex) { return false; }
    }

    @Test
    void fulfillmentExcludesExpiredStockAndCannotDeductTwice() {
        Hospital h = new Hospital(); h.setName("Test hospital"); h.setLocation("Bengaluru");
        h.setEmail(UUID.randomUUID() + "@example.org"); h.setRegistrationNo(UUID.randomUUID().toString());
        h.setPassword("test hash"); hospitalRepo.save(h);
        BloodStock expired = stock("A-", 10, LocalDate.now().minusDays(1));
        BloodStock usable = stock("A-", 2, LocalDate.now());
        BloodRequest req = requestRepo.save(BloodRequestFactory.createRequest(h, "A-", 3, "Critical"));
        assertThrows(IllegalStateException.class, () -> inventory.fulfillRequest(req.getRequestId(), new FIFOAllocationStrategy()));
        assertEquals(2, stockRepo.findById(usable.getStockId()).orElseThrow().getQuantity());
        assertEquals("Pending", requestRepo.findById(req.getRequestId()).orElseThrow().getStatus());
        usable.setQuantity(3); stockRepo.save(usable);
        assertTrue(inventory.fulfillRequest(req.getRequestId(), new PriorityAllocationStrategy()));
        assertEquals(0, stockRepo.findById(usable.getStockId()).orElseThrow().getQuantity());
        assertEquals(10, stockRepo.findById(expired.getStockId()).orElseThrow().getQuantity());
        assertThrows(IllegalStateException.class, () -> inventory.fulfillRequest(req.getRequestId(), new FIFOAllocationStrategy()));
        assertEquals("Fulfilled", requestRepo.findById(req.getRequestId()).orElseThrow().getStatus());
        assertEquals(1, inventory.removeExpiredStock());
        assertTrue(stockRepo.findById(expired.getStockId()).isEmpty());
        assertTrue(stockRepo.findById(usable.getStockId()).isPresent());
    }

    @Test
    void allocationStrategiesChooseDifferentBatchOrders() {
        BloodStock older = stock("AB-", 2, LocalDate.now().plusDays(20));
        BloodStock sooner = stock("AB-", 2, LocalDate.now().plusDays(2));
        sooner.setDateAdded(LocalDate.now()); stockRepo.save(sooner);
        var fifo = new FIFOAllocationStrategy();
        var priority = new PriorityAllocationStrategy();
        var tx = new org.springframework.transaction.support.TransactionTemplate(txManager);
        tx.executeWithoutResult(status -> {
            assertEquals(older.getStockId(), fifo.allocate("AB-", LocalDate.now(), stockRepo).get(0).getStockId());
            assertEquals(sooner.getStockId(), priority.allocate("AB-", LocalDate.now(), stockRepo).get(0).getStockId());
        });
    }

    BloodStock stock(String type, double qty, LocalDate expiry) {
        BloodStock s = new BloodStock(); s.setInventory(inventory.getDefaultInventory());
        s.setBloodType(type); s.setQuantity(qty); s.setDateAdded(LocalDate.now().minusDays(42));
        s.setExpiryDate(expiry); return stockRepo.save(s);
    }
}
