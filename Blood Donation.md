# Blood Donation and Management System

**UE23CS352B – Object Oriented Analysis & Design**  
**Mini-Project Report**

---

## Course Details

- **Course Code:** UE23CS352B
- **Course Name:** Object Oriented Analysis & Design
- **Domain:** Healthcare

---

## Team Members

| S.No | Name | SRN | 
|------|------|-----|
| 1 | Satwik R Bankapur | PES2UG23CS539 |
| 2 | Sathwik HS | PES2UG23CS536 |
| 3 | Prajwal K | PES234UG23CS818 | 
| 4 | Sudarshan JN | PES234UG23CS825 | 

---
## Table of Contents

1. [Problem Statement](#problem-statement)
2. [Project Synopsis](#project-synopsis)
3. [Functional Requirements](#functional-requirements)
4. [System Architecture](#system-architecture)
5. [UML Diagrams](#uml-diagrams)
6. [Design Patterns](#design-patterns)
7. [Design Principles](#design-principles)
8. [Technology Stack](#technology-stack)
9. [Database Design](#database-design)
10. [Implementation Details](#implementation-details)
11. [Individual Contributions](#individual-contributions)
12. [GitHub Repository](#github-repository)
13. [Screenshots](#screenshots)

---

## Problem Statement
Blood donation and management is a critical aspect of healthcare infrastructure. The current systems often lack coordination between blood donors, donation camps, hospitals, and blood banks, leading to:

- Inefficient blood inventory management
- Delayed response to urgent blood requirements
- Poor tracking of donation camps and donor registrations
- Lack of real-time visibility of blood availability
- Difficulty in managing rare blood types and expiry dates
- Manual processes prone to errors

**Objective:** Develop a comprehensive Blood Donation and Management System that streamlines the entire blood donation lifecycle from camp organization to blood distribution, ensuring efficient inventory management and timely fulfillment of hospital requirements.

---

## Project Synopsis
The Blood Donation and Management System is designed to digitize and optimize the blood donation ecosystem. The system connects four major stakeholders: Organisers, Donators, Hospitals, and Inventory Managers, providing a unified platform for blood donation activities.

### Key Capabilities:

- **Camp Management:** Organisers can create, schedule, and manage blood donation camps
- **Donor Registration:** Donors can register for camps, track eligibility, and view donation history
- **Blood Requests:** Hospitals can request blood with severity levels and priority
- **Inventory Management:** Real-time tracking of blood stock with expiry dates, low stock alerts for rare and normal blood types
- **Automated Matching:** System automatically matches hospital requests with available inventory
- **Notifications:** Alerts for low stock, expired blood, and urgent hospital requirements

---

## Functional Requirements
### User Roles

The system supports four primary user types with distinct functionalities:

#### 1. Organiser

- **Authentication:** Register and login to the system
- **Camp Management:** Create, update, and cancel blood donation camps
- **Camp Details:** Define camp name, location, date, time, and capacity
- **Monitor Registrations:** View list of registered donors for each camp
- **Reports:** Generate camp-wise donation reports

#### 2. Donator (Donor)

- **Authentication:** Register with blood type and personal details, login
- **Profile Management:** Update personal information and medical history
- **Eligibility Check:** System verifies eligibility based on age, last donation date (90-day rule)
- **Camp Discovery:** Browse available donation camps by location and date
- **Registration:** Register for camps (subject to capacity limits)
- **Donation:** Record donation after attending camp
- **History:** View complete donation history and upcoming appointments
- **Certificates:** Download donation certificates

#### 3. Hospital

- **Authentication:** Register with hospital credentials, login
- **Blood Request:** Create urgent or routine blood requests
- **Request Details:** Specify blood type, quantity (in units), and severity level (Critical/Urgent/Routine)
- **Priority System:** Critical requests get highest priority in fulfillment
- **Availability Check:** Real-time check of blood availability
- **Request Tracking:** Monitor status of all requests (Pending/Approved/Fulfilled/Rejected)
- **Receive Blood:** Confirm receipt of blood units
- **Reports:** View request history and fulfillment time analytics

#### 4. Inventory Manager

- **Authentication:** Login with manager credentials
- **Stock Management:** Add, update, and remove blood stock entries
- **Stock Details:** Manage blood type, quantity, expiry date, and rare blood classification
- **Low Stock Alerts:** Automatic notifications when stock falls below threshold
- **Expiry Management:** Identify and remove expired blood units
- **Request Fulfillment:** Process hospital requests based on availability and priority
- **Dashboard:** Real-time view of all blood types with quantities
- **Reports:** Generate inventory reports, donation trends, and utilization statistics
### Major Features (4 Use Cases - 1 per member)

#### Feature 1: Camp Organization and Management

- **Owner:** Team Member 1
- **Description:** Complete lifecycle management of blood donation camps

**Functional Flow:**
1. Organiser creates camp with details (location, date, capacity)
2. System validates date availability and sends notifications
3. Donors register for camps
4. System tracks registrations against capacity
5. Post-camp, donations are recorded and linked to camp
6. Organiser generates camp reports
#### Feature 2: Donor Registration and Donation Process

- **Owner:** Team Member 2
- **Description:** End-to-end donor registration, eligibility, and donation workflow

**Functional Flow:**
1. Donor registers in system with blood type
2. Donor searches for camps by location/date
3. System checks eligibility (age, last donation date)
4. Donor registers for eligible camp
5. At camp, donation is recorded with quantity
6. System updates donor's last donation date
7. Donation record sent to inventory
#### Feature 3: Hospital Blood Request Management

- **Owner:** Team Member 3
- **Description:** Hospital request creation, prioritization, and fulfillment

**Functional Flow:**
1. Hospital creates blood request with type, quantity, severity
2. System checks inventory availability
3. Request prioritized based on severity
4. Inventory manager reviews and approves request
5. Blood units allocated to hospital
6. Hospital confirms receipt
7. System updates inventory
#### Feature 4: Inventory and Stock Management

- **Owner:** Team Member 4
- **Description:** Comprehensive blood stock management with alerts and analytics

**Functional Flow:**
1. New donations added to inventory automatically
2. System categorizes by blood type (rare/normal)
3. Low stock alerts generated when below threshold
4. Expiry tracking with automated removal alerts
5. Inventory manager fulfills hospital requests
6. System maintains audit log of all transactions
7. Real-time dashboard with stock levels
### Minor Features (4 Additional Use Cases)

#### 1. Donor Eligibility Verification
- Automated checking of donation eligibility
- Validation rules: Age (18-65), minimum 90 days since last donation
- Health questionnaire integration

#### 2. Blood Type Compatibility Checker
- Cross-matching logic for blood type compatibility
- Alternative blood type suggestions when exact match unavailable

#### 3. Notification System
- Email/SMS notifications for camp registrations
- Low stock alerts to organisers and inventory managers
- Urgent request notifications to admin

#### 4. Analytics and Reports Dashboard
- Donation trends over time
- Camp effectiveness metrics
- Hospital request fulfillment rate
- Donor retention statistics

---

## System Architecture
### MVC Architecture Pattern

The system follows the Model-View-Controller (MVC) architectural pattern using the Java Spring Framework:

#### Model Layer

- **Entities:** Organiser, Camp, Donator, Donation, Hospital, BloodRequest, Inventory, BloodStock, Registration
- **Repository Layer:** Spring Data JPA repositories for database operations
- **Service Layer:** Business logic implementation
  - CampService, DonatorService, HospitalService, InventoryService
- **DTOs:** Data Transfer Objects for API communication

#### View Layer

**Frontend Options:**
- Option 1: JavaFX for desktop application
- Option 2: Thymeleaf templates for web application
- Option 3: REST API + React/Angular (optional)

**UI Components:** Forms, tables, dashboards, charts  
**Responsive Design:** Bootstrap CSS framework

#### Controller Layer

- **REST Controllers:** Handle HTTP requests
  - CampController, DonatorController, HospitalController, InventoryController
- **Request Mapping:** URL routing to appropriate service methods
- **Validation:** Input validation using JSR-303/Bean Validation
- **Exception Handling:** Global exception handlers

#### Flow Example: Donor Registration for Camp

1. **View (UI Form)** → User fills registration form
2. **Controller** → DonatorController.registerForCamp(campId, donorId)
3. **Controller** → Validates input, checks authentication
4. **Service** → DonatorService.registerForCamp() - Business logic
5. **Service** → Checks donor eligibility
6. **Service** → Checks camp capacity
7. **Model** → Creates Registration entity
8. **Repository** → Saves to database
9. **Service** → Returns success/failure
10. **Controller** → Returns ResponseEntity
11. **View** → Displays confirmation message

---
## UML Diagrams

### 1. Use Case Diagram (1 Required)

**Actors:**
- Organiser
- Donator
- Hospital
- Inventory Manager

**Use Cases:**
- **Organiser:** Create Camp, Cancel Camp, View Registrations
- **Donator:** Register Account, Register for Camp, Donate Blood, View History
- **Hospital:** Create Blood Request, Track Request, Receive Blood
- **Inventory Manager:** Manage Stock, Fulfill Request, Generate Alerts

*(Refer to Use Case Diagram image/file)*

### 2. Class Diagram (1 Required)

The complete class diagram is available in `BloodDonationSystem_UML.drawio` file.

**Key Classes:**

- **Organiser:** Manages camps
- **Camp:** Blood donation camp entity
- **Donator:** Blood donor
- **Donation:** Individual donation record
- **Registration:** Association class linking Donator and Camp
- **Hospital:** Healthcare facility
- **BloodRequest:** Blood requirement from hospital
- **Inventory:** Central inventory management
- **BloodStock:** Individual blood type stock

**Key Relationships:**

- Organiser organizes Camp (1 to many)
- Donator registers forCamp (many to many via Registration)
- Donator makes Donation (1 to many)
- Camp has Donations (1 to many)
- Donation updates Inventory (many to 1)
- Hospital creates BloodRequest (1 to many)
- BloodRequest fulfilled by Inventory (many to 1)
- Inventory manages BloodStock (1 to many)

### 3. Activity Diagrams (4 Required - 1 per major use case)
#### Activity Diagram 1: Camp Organization Process

**Owner:** Team Member 1

**Activities:**
1. Start
2. Organiser logs in
3. Navigate to Create Camp
4. Enter camp details (name, location, date, time, capacity)
5. System validates date (not in past)
6. System checks for scheduling conflicts
7. [Valid] → Save camp to database
8. [Invalid] → Show error, return to form
9. Send notifications to registered donors
10. Display success message
11. End
#### Activity Diagram 2: Donor Registration and Donation

**Owner:** Team Member 2

**Activities:**
1. Start
2. Donor logs in
3. Browse available camps
4. Select camp
5. System checks eligibility
6. Check age (18-65)
7. Check last donation date (>90 days)
8. [Eligible] → Check camp capacity
9. [Not Full] → Create registration
10. [Full] → Show "Camp Full" message
11. Confirmation sent to donor
12. On camp day, donor attends
13. Health screening
14. [Passed] → Blood donation
15. Record donation (quantity, blood type)
16. Update inventory
17. Generate donation certificate
18. End
#### Activity Diagram 3: Hospital Blood Request Fulfillment

**Owner:** Team Member 3

**Activities:**
1. Start
2. Hospital logs in
3. Create new blood request
4. Enter details (blood type, quantity, severity)
5. Submit request
6. System checks inventory availability
7. [Available] → Request marked as "Approved"
8. [Not Available] → Request marked as "Pending"
9. Inventory manager reviews requests
10. Prioritize by severity (Critical > Urgent > Routine)
11. Allocate blood units
12. Update request status to "Fulfilled"
13. Reduce inventory quantity
14. Notify hospital
15. Hospital confirms receipt
16. End
#### Activity Diagram 4: Inventory Management and Stock Alerts

**Owner:** Team Member 4

**Activities:**
1. Start
2. New donation received
3. Add to inventory with blood type
4. Set expiry date (42 days from collection)
5. Categorize as rare/normal blood type
6. Update stock quantity
7. Check if quantity < threshold
8. [Low Stock] → Generate alert
9. Notify organisers to schedule camps
10. Daily scheduled job runs
11. Check for expired blood
12. [Expired Found] → Mark for removal
13. Generate expiry report
14. Inventory manager reviews
15. Remove expired units
16. Update dashboard
17. End

### 4. State Diagrams (4 Required)
#### State Diagram 1: Camp States

**Owner:** Team Member 1

**States:**
- **Draft:** Initial state, camp created but not published
- **Scheduled:** Camp published and accepting registrations
- **Registration Closed:** Camp full or registration deadline passed
- **In Progress:** Camp currently happening
- **Completed:** Camp finished, donations recorded
- **Cancelled:** Camp cancelled by organiser

**Transitions:**
- Draft → Scheduled [publish]
- Scheduled → Registration Closed [capacity reached OR deadline passed]
- Scheduled → In Progress [camp date reached]
- In Progress → Completed [camp ended]
- Scheduled → Cancelled [organiser cancels]
- Draft → Cancelled [organiser cancels]
#### State Diagram 2: Donor Eligibility States

**Owner:** Team Member 2

**States:**
- **New:** Newly registered donor, never donated
- **Eligible:** Can donate (>90 days since last donation)
- **Ineligible:** Cannot donate (temporary - within 90 days)
- **Permanently Ineligible:** Medical reasons, age restrictions
- **Donated:** Recently donated (transitions to Ineligible for 90 days)

**Transitions:**
- New → Eligible [age verified, health check passed]
- Eligible → Donated [donation completed]
- Donated → Ineligible [automatic transition]
- Ineligible → Eligible [90 days elapsed]
- Eligible → Permanently Ineligible [medical disqualification]
#### State Diagram 3: Blood Request States

**Owner:** Team Member 3

**States:**
- **Created:** Request submitted by hospital
- **Pending:** Awaiting inventory availability
- **Approved:** Inventory available, awaiting allocation
- **Allocated:** Blood units reserved for hospital
- **Fulfilled:** Blood delivered to hospital
- **Rejected:** Cannot fulfill request
- **Cancelled:** Hospital cancelled request

**Transitions:**
- Created → Pending [submitted]
- Pending → Approved [inventory check passed]
- Pending → Rejected [insufficient stock]
- Approved → Allocated [inventory manager allocates]
- Allocated → Fulfilled [hospital receives blood]
- Pending → Cancelled [hospital cancels]
- Approved → Cancelled [hospital cancels]
#### State Diagram 4: Blood Stock States

**Owner:** Team Member 4

**States:**
- **Available:** Fresh stock, available for requests
- **Reserved:** Allocated to hospital request
- **Low Stock:** Quantity below threshold
- **Expired:** Past expiry date
- **Issued:** Given to hospital
- **Discarded:** Removed from inventory

**Transitions:**
- Available → Reserved [allocated to request]
- Reserved → Issued [hospital receives]
- Available → Low Stock [quantity < threshold]
- Low Stock → Available [restocked]
- Available → Expired [expiry date passed]
- Low Stock → Expired [expiry date passed]
- Expired → Discarded [removed by manager]

---

## ## Design Patterns

The project implements various design patterns (minimum 4: Creational, Structural, Behavioral, and Framework):

### 1. Singleton Pattern (Creational)

**Owner:** Team Member 4  
**Implementation:** InventoryManager

```java
public class InventoryManager {
    private static InventoryManager instance;
    private Map<String, BloodStock> stockMap;
    
    private InventoryManager() {
        stockMap = new HashMap<>();
    }
    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }
    
    public void addStock(BloodStock stock) {
        // Implementation
    }
}
```

**Justification:** Ensures single instance of inventory manager to maintain consistent state of blood stock across the application.

### 2. Factory Pattern (Creational)

**Owner:** Team Member 3  
**Implementation:** BloodRequestFactory

```java
public class BloodRequestFactory {
    public static BloodRequest createRequest(
        String type, Hospital hospital, String bloodType, 
        double quantity, String severity) {
        
        switch(severity.toLowerCase()) {
            case "critical":
                return new CriticalBloodRequest(hospital, bloodType, quantity);
            case "urgent":
                return new UrgentBloodRequest(hospital, bloodType, quantity);
            case "routine":
                return new RoutineBloodRequest(hospital, bloodType, quantity);
            default:
                throw new IllegalArgumentException("Invalid severity");
        }
    }
}
```

**Justification:** Simplifies creation of different types of blood requests with varying priority levels and handling logic.

### 3. Observer Pattern (Behavioral)

**Owner:** Team Member 1  
**Implementation:** NotificationObserver for camp updates

```java
public interface Observer {
    void update(String message);
}

public class Camp extends Subject {
    private List<Observer> observers = new ArrayList<>();
    
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
    
    public void publishCamp() {
        // Publish logic
        notifyObservers("New camp available: " + this.name);
    }
}

public class Donator implements Observer {
    @Override
    public void update(String message) {
        // Send notification to donor
        System.out.println("Notification: " + message);
    }
}
```

**Justification:** Donors automatically receive notifications when new camps are created or existing camps are updated.

### 4. Strategy Pattern (Behavioral)

**Owner:** Team Member 4  
**Implementation:** Stock Allocation Strategy

```java
public interface AllocationStrategy {
    BloodStock allocate(List<BloodStock> availableStock, double quantity);
}

public class FIFOAllocationStrategy implements AllocationStrategy {
    @Override
    public BloodStock allocate(List<BloodStock> availableStock, double quantity) {
        // Allocate oldest stock first
        return availableStock.stream()
            .sorted(Comparator.comparing(BloodStock::getDateAdded))
            .findFirst()
            .orElse(null);
    }
}

public class PriorityAllocationStrategy implements AllocationStrategy {
    @Override
    public BloodStock allocate(List<BloodStock> availableStock, double quantity) {
        // Prioritize stock closer to expiry
        return availableStock.stream()
            .sorted(Comparator.comparing(BloodStock::getExpiryDate))
            .findFirst()
            .orElse(null);
    }
}
```

**Justification:** Allows flexible blood allocation strategies (FIFO, nearest expiry, etc.) based on inventory policy.

### 5. Adapter Pattern (Structural)

**Owner:** Team Member 2  
**Implementation:** External notification service adapter

```java
public interface NotificationService {
    void sendNotification(String recipient, String message);
}

public class EmailServiceAdapter implements NotificationService {
    private ExternalEmailAPI emailAPI;
    
    @Override
    public void sendNotification(String recipient, String message) {
        emailAPI.sendEmail(recipient, "Blood Donation", message);
    }
}

public class SMSServiceAdapter implements NotificationService {
    private ExternalSMSAPI smsAPI;
    
    @Override
    public void sendNotification(String recipient, String message) {
        smsAPI.sendSMS(recipient, message);
    }
}
```

**Justification:** Adapts different external notification APIs (Email, SMS) to a common interface used by the application.

### 6. Dependency Injection (Framework Pattern - Spring)

**Owner:** All team members  
**Implementation:** Spring Framework's built-in DI

```java
@Service
public class DonatorService {
    
    @Autowired
    private DonatorRepository donatorRepository;
    
    @Autowired
    private CampRepository campRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public void registerForCamp(Long donatorId, Long campId) {
        // Business logic using injected dependencies
    }
}
```

**Justification:** Promotes loose coupling, easier testing, and better maintainability through Spring's IoC container.

---

## ## Design Principles

The project adheres to SOLID and other key design principles:

### 1. Single Responsibility Principle (SRP)

**Owner:** Team Member 1

**Implementation:**
- Each class has one responsibility
- CampService only handles camp business logic
- CampRepository only handles data persistence
- CampController only handles HTTP requests/responses

**Example:**

```java
// BEFORE (Violating SRP)
public class Camp {
    public void createCamp() { }
    public void saveToDatabase() { }
    public void sendEmailNotifications() { }
}

// AFTER (Following SRP)
public class Camp {
    // Only camp data and behavior
}

public class CampService {
    public void createCamp() { }
}

public class NotificationService {
    public void sendEmailNotifications() { }
}
```

### 2. Open/Closed Principle (OCP)

**Owner:** Team Member 3

**Implementation:** Blood request severi handling

```java
// Open for extension, closed for modification
public abstract class BloodRequest {
    protected String bloodType;
    protected double quantity;
    public abstract int getPriority();
    public abstract String getProcessingMessage();
}

public class CriticalBloodRequest extends BloodRequest {
    @Override
    public int getPriority() { return 1; }
    
    @Override
    public String getProcessingMessage() {
        return "CRITICAL: Immediate processing required";
    }
}

public class UrgentBloodRequest extends BloodRequest {
    @Override
    public int getPriority() { return 2; }
    
    @Override
    public String getProcessingMessage() {
        return "URGENT: Process within 24 hours";
    }
}
```

**Justification:** New severity types can be added by extending BloodRequest without modifying existing code.

### 3. Liskov Substitution Principle (LSP)

**Owner:** Team Member 2

**Implementation:** User hierarchy

```java
public abstract class User {
    protected String userId;
    protected String name;
    protected String email;
    
    public abstract void login();
    public abstract void logout();
}

public class Donator extends User {
    @Override
    public void login() {
        // Donator specific login
    }
    
    @Override
    public void logout() {
        // Donator specific logout
    }
    
    public void registerForCamp() {
        // Donator specific functionality
    }
}

public class Hospital extends User {
    @Override
    public void login() {
        // Hospital specific login
    }
    
    @Override
    public void logout() {
        // Hospital specific logout
    }
    
    public void requestBlood() {
        // Hospital specific functionality
    }
}
```

**Justification:** Any User subclass can be substituted without breaking authentication functionality.

### 4. Interface Segregation Principle (ISP)

**Owner:** Team Member 4

**Implementation:** Segregated interfaces for inventory operations

```java
// BEFORE (Fat Interface)
public interface InventoryOperations {
    void addStock();
    void removeStock();
    void checkAvailability();
    void generateReport();
    void sendAlert();
    void fulfillRequest();
}

// AFTER (Segregated Interfaces)
public interface StockManagement {
    void addStock();
    void removeStock();
}

public interface StockQuery {
    void checkAvailability();
}

public interface ReportGeneration {
    void generateReport();
}

public interface AlertSystem {
    void sendAlert();
}

public class InventoryService implements StockManagement, StockQuery, AlertSystem {
    // Implements only needed interfaces
}
```

**Justification:** Classes implement only the interfaces they need, avoiding unnecessary method implementations.

### 5. Dependency Inversion Principle (DIP)

**Owner:** All team members

**Implementation:** Depending on abstractions, not concretions

```java
// High-level module depends on abstraction
public class DonationService {
    private final InventoryRepository inventoryRepo; // Interface
    private final NotificationService notificationService; // Interface
    
    @Autowired
    public DonationService(
        InventoryRepository inventoryRepo,
        NotificationService notificationService) {
        
        this.inventoryRepo = inventoryRepo;
        this.notificationService = notificationService;
    }
    
    public void processDonation(Donation donation) {
        inventoryRepo.addStock(donation.getBloodStock());
        notificationService.sendNotification("Donation processed");
    }
}
```

**Justification:** High-level modules (services) depend on abstractions (interfaces), making the system flexible and testable.

### 6. DRY (Don't Repeat Yourself)

**Owner:** All team members

**Implementation:** Common validation logic extracted

```java
public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public static boolean isValidBloodType(String bloodType) {
        return Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                     .contains(bloodType);
    }
    
    public static boolean isEligibleAge(int age) {
        return age >= 18 && age <= 65;
    }
}
```

**Justification:** Common validation logic is centralized and reused across the application.

---

## ## Technology Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17
- **ORM:** Spring Data JPA / Hibernate
- **Build Tool:** Maven / Gradle
- **Security:** Spring Security (Authentication & Authorization)
- **Validation:** Hibernate Validator (JSR-303)

### Frontend
- **Option 1 (Desktop):** JavaFX with FXML
- **Option 2 (Web):** Thymeleaf + Bootstrap 5
- **Option 3 (Modern Web):** React.js / Angular (consuming REST API)

### Database
- **Development:** H2 Database (in-memory)
- **Production:** MySQL 8.0 / PostgreSQL 14

### Additional Tools
- **API Documentation:** Swagger / Springdoc OpenAPI
- **Logging:** SLF4J with Logback
- **Testing:** JUnit 5, Mockito
- **Version Control:** Git / GitHub
- **IDE:** IntelliJ IDEA / Eclipse

---

## Database Design

### Entity-Relationship Overview

### Tables

#### 1. organiser

```sql
CREATE TABLE organiser (
    organiser_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(15),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

#### 2. camp

```sql
CREATE TABLE camp (
    camp_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    organiser_id VARCHAR(50),
    FOREIGN KEY (organiser_id) REFERENCES organiser(organiser_id)
);
```

#### 3. donator

```sql
CREATE TABLE donator (
    donor_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    contact VARCHAR(15),
    last_donation_date DATE,
    is_eligible BOOLEAN DEFAULT TRUE,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

#### 4. registration

```sql
CREATE TABLE registration (
    registration_id VARCHAR(50) PRIMARY KEY,
    donor_id VARCHAR(50) NOT NULL,
    camp_id VARCHAR(50) NOT NULL,
    registration_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES donator(donor_id),
    FOREIGN KEY (camp_id) REFERENCES camp(camp_id)
);
```

#### 5. donation

```sql
CREATE TABLE donation (
    donation_id VARCHAR(50) PRIMARY KEY,
    donor_id VARCHAR(50) NOT NULL,
    camp_id VARCHAR(50),
    donation_date DATE NOT NULL,
    quantity DOUBLE NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES donator(donor_id),
    FOREIGN KEY (camp_id) REFERENCES camp(camp_id)
);
```

#### 6. hospital

```sql
CREATE TABLE hospital (
    hospital_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    location VARCHAR(255) NOT NULL,
    contact VARCHAR(15),
    registration_no VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```

#### 7. blood_request

```sql
CREATE TABLE blood_request (
    request_id VARCHAR(50) PRIMARY KEY,
    hospital_id VARCHAR(50) NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    quantity DOUBLE NOT NULL,
    severity VARCHAR(50) NOT NULL,
    request_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    priority INT NOT NULL,
    FOREIGN KEY (hospital_id) REFERENCES hospital(hospital_id)
);
```

#### 8. inventory

```sql
CREATE TABLE inventory (
    inventory_id VARCHAR(50) PRIMARY KEY,
    location VARCHAR(255) NOT NULL
);
```

#### 9. blood_stock

```sql
CREATE TABLE blood_stock (
    stock_id VARCHAR(50) PRIMARY KEY,
    inventory_id VARCHAR(50) NOT NULL,
    blood_type VARCHAR(5) NOT NULL,
    quantity DOUBLE NOT NULL,
    expiry_date DATE NOT NULL,
    is_rare BOOLEAN DEFAULT FALSE,
    low_stock_threshold DOUBLE NOT NULL,
    date_added DATE NOT NULL,
    FOREIGN KEY (inventory_id) REFERENCES inventory(inventory_id)
);
```

---

## Implementation Details

### Project Structure

```
blood-donation-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.blooddonation/
│   │   │       ├── BloodDonationApplication.java
│   │   │       ├── controller/
│   │   │       │   ├── CampController.java
│   │   │       │   ├── DonatorController.java
│   │   │       │   ├── HospitalController.java
│   │   │       │   └── InventoryController.java
│   │   │       ├── model/
│   │   │       │   ├── Organiser.java
│   │   │       │   ├── Camp.java
│   │   │       │   ├── Donator.java
│   │   │       │   ├── Donation.java
│   │   │       │   ├── Registration.java
│   │   │       │   ├── Hospital.java
│   │   │       │   ├── BloodRequest.java
│   │   │       │   ├── Inventory.java
│   │   │       │   └── BloodStock.java
│   │   │       ├── repository/
│   │   │       │   ├── CampRepository.java
│   │   │       │   ├── DonatorRepository.java
│   │   │       │   ├── HospitalRepository.java
│   │   │       │   └── InventoryRepository.java
│   │   │       ├── service/
│   │   │       │   ├── CampService.java
│   │   │       │   ├── DonatorService.java
│   │   │       │   ├── HospitalService.java
│   │   │       │   └── InventoryService.java
│   │   │       ├── dto/
│   │   │       │   ├── CampDTO.java
│   │   │       │   ├── DonationDTO.java
│   │   │       │   └── BloodRequestDTO.java
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── DatabaseConfig.java
│   │   │       ├── exception/
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       └── util/
│   │   │           └── ValidationUtils.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── templates/ (if using Thymeleaf)
│   │       └── static/
│   │           ├── css/
│   │           └── js/
│   └── test/
│       └── java/
│           └── com.blooddonation/
│               ├── CampServiceTest.java
│               ├── DonatorServiceTest.java
│               ├── HospitalServiceTest.java
│               └── InventoryServiceTest.java
│
├── pom.xml / build.gradle
└── README.md
```

### Key Implementation Highlights

#### 1. Spring Boot Configuration

```java
@SpringBootApplication
@EnableJpaRepositories
public class BloodDonationApplication {
    public static void main(String[] args) {
        SpringApplication.run(BloodDonationApplication.class, args);
    }
}
```

#### 2. JPA Entity Example

```java
@Entity
@Table(name = "donator")
public class Donator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String donorId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private int age;
    
    @Column(nullable = false)
    private String bloodType;
    
    @Column
    private LocalDate lastDonationDate;
    
    @OneToMany(mappedBy = "donator", cascade = CascadeType.ALL)
    private List<Donation> donations;
    
    @ManyToMany
    @JoinTable(
        name = "registration",
        joinColumns = @JoinColumn(name = "donor_id"),
        inverseJoinColumns = @JoinColumn(name = "camp_id")
    )
    private List<Camp> camps;
    
    // Getters, setters, constructors
}
```

#### 3. Service Layer Example

```java
@Service
@Transactional
public class DonatorService {
    
    @Autowired
    private DonatorRepository donatorRepository;
    
    @Autowired
    private CampRepository campRepository;
    
    public boolean checkEligibility(String donorId) {
        Donator donator = donatorRepository.findById(donorId)
            .orElseThrow(() -> new ResourceNotFoundException("Donor not found"));
        
        // Check age
        if (donator.getAge() < 18 || donator.getAge() > 65) {
            return false;
        }
        
        // Check last donation date (90 days rule)
        if (donator.getLastDonationDate() != null) {
            long daysSinceLastDonation = ChronoUnit.DAYS.between(
                donator.getLastDonationDate(), 
                LocalDate.now()
            );
            return daysSinceLastDonation >= 90;
        }
        
        return true;
    }
    
    public void registerForCamp(String donorId, String campId) {
        // Implementation with validation
    }
}
```

#### 4. REST Controller Example

```java
@RestController
@RequestMapping("/api/camps")
public class CampController {
    
    @Autowired
    private CampService campService;
    
    @PostMapping
    public ResponseEntity<CampDTO> createCamp(@Valid @RequestBody CampDTO campDTO) {
        Camp camp = campService.createCamp(campDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(camp));
    }
    
    @GetMapping
    public ResponseEntity<List<CampDTO>> getAllCamps() {
        List<Camp> camps = campService.getAllCamps();
        return ResponseEntity.ok(camps.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CampDTO> getCampById(@PathVariable String id) {
        Camp camp = campService.getCampById(id);
        return ResponseEntity.ok(convertToDTO(camp));
    }
    
    private CampDTO convertToDTO(Camp camp) {
        // Conversion logic
    }
}
```

---

## ## Individual Contributions

### Team Member 1: [Name] - Camp Organization Module

**Responsibility:** Complete camp lifecycle management

**Contributions:**
- Designed and implemented Camp and Organiser entities
- Developed CampService with business logic for camp creation, updating, cancellation
- Implemented CampController with RESTful endpoints
- Created Observer pattern for camp notifications
- Developed Activity Diagram for camp organization process
- Implemented State Diagram for camp states
- Applied Single Responsibility Principle across camp module
- Unit tests for CampService

**Files Owned:**
- Camp.java, Organiser.java
- CampService.java, CampController.java
- CampRepository.java
- CampObserver.java, CampSubject.java

### Team Member 2: [Name] - Donor Management Module

**Responsibility:** Donor registration, eligibility, and donation workflow

**Contributions:**
- Designed and implemented Donator and Registration entities
- Developed DonatorService with eligibility checking logic
- Implemented donor registration for camps with capacity validation
- Created Adapter pattern for notification services
- Developed Activity Diagram for donor registration and donation
- Implemented State Diagram for donor eligibility states
- Applied Liskov Substitution Principle in user hierarchy
- Integration tests for donor workflow

**Files Owned:**
- Donator.java, Registration.java
- DonatorService.java, DonatorController.java
- DonatorRepository.java
- NotificationServiceAdapter.java

### Team Member 3: [Name] - Hospital Request Module

**Responsibility:** Hospital blood request management and fulfillment

**Contributions:**
- Designed and implemented Hospital and BloodRequest entities
- Developed HospitalService with request prioritization logic
- Implemented Factory pattern for creating different request types
- Created severity-based priority system (Critical/Urgent/Routine)
- Developed Activity Diagram for blood request fulfillment
- Implemented State Diagram for blood request states
- Applied Open/Closed Principle with extensible request types
- Unit tests for request prioritization logic

**Files Owned:**
- Hospital.java, BloodRequest.java
- HospitalService.java, HospitalController.java
- BloodRequestFactory.java
- CriticalBloodRequest.java, UrgentBloodRequest.java, RoutineBloodRequest.java

### Team Member 4: [Name] - Inventory Management Module

**Responsibility:** Blood stock management, alerts, and analytics

**Contributions:**
- Designed and implemented Inventory and BloodStock entities
- Developed InventoryService with stock management and expiry tracking
- Implemented Singleton pattern for InventoryManager
- Created Strategy pattern for stock allocation strategies
- Implemented low stock alerts for rare and normal blood types
- Developed Activity Diagram for inventory management
- Implemented State Diagram for blood stock states
- Applied Interface Segregation Principle for inventory operations
- Unit tests for stock allocation strategies

**Files Owned:**
- Inventory.java, BloodStock.java
- InventoryService.java, InventoryController.java
- InventoryManager.java (Singleton)
- AllocationStrategy.java, FIFOAllocationStrategy.java, PriorityAllocationStrategy.java

---

## GitHub Repository

**Repository URL:** [To be added]

**Repository Structure:**
- `main` branch: Production-ready code
- `develop` branch: Integration branch
- Feature branches: `feature/camp-management`, `feature/donor-module`, etc.

**Commit Guidelines:**
- Use meaningful commit messages
- Reference issue numbers in commits
- Code review required before merging to develop

**Documentation:**
- README.md with setup instructions
- API documentation using Swagger
- Javadoc for all public methods

---

## ## Screenshots

### 1. Home Page / Dashboard

![Dashboard]()

- Overview of system statistics
- Quick access to major features
- Real-time blood stock levels

### 2. Camp Creation Form (Organiser)

![Camp Creation]()

- Form with validation
- Date picker for camp date
- Capacity input with validation

### 3. Camp List View (Donor)

![Camp List]()

- Available camps displayed with details
- Filter by location and date
- Register button for eligible donors

### 4. Donor Registration Form

![Donor Registration]()

- Required fields: Name, Age, Blood Type, Contact
- Email verification
- Password with strength indicator

### 5. Donation History View

![Donation History]()

- Table showing past donations
- Date, camp name, quantity donated
- Download certificate option

### 6. Blood Request Form (Hospital)

![Blood Request]()

- Blood type dropdown
- Quantity input (in units)
- Severity selection (Critical/Urgent/Routine)
- Submit button

### 7. Hospital Request Dashboard

![Hospital Dashboard]()

- List of all requests with status
- Color-coded by severity
- Track fulfillment progress

### 8. Inventory Dashboard

![Inventory Dashboard]()

- Blood stock levels by type (A+, A-, B+, etc.)
- Visual indicators for low stock
- Expiry alerts highlighted in red
- Rare blood types marked with special icon

### 9. Blood Stock Management

![Stock Management]()

- Add new stock form
- Edit/delete existing stock
- Filter by blood type
- Sort by expiry date

### 10. Low Stock Alert View

![Low Stock Alerts]()

- List of blood types below threshold
- Recommended action (schedule camps)
- Notification sent to organisers

### 11. Reports and Analytics

![Analytics Dashboard]()

- Donation trends chart (line graph)
- Camp effectiveness metrics
- Hospital request fulfillment rate
- Top donators leaderboard

### 12. User Login Page

![Login Page]()

- Role-based login (Organiser/Donor/Hospital/Admin)
- Forgot password link
- Register new account link

---

## Testing and Quality Assurance

### Unit Testing

- JUnit 5 for service layer testing
- Mockito for mocking dependencies
- Target: >80% code coverage

### Integration Testing

- Spring Boot Test for integration tests
- Test database: H2 in-memory
- Test all API endpoints

### Test Cases Examples:

#### Donor Eligibility Test
- Test age < 18 → ineligible
- Test last donation < 90 days → ineligible
- Test valid donor → eligible

#### Camp Capacity Test
- Test registration when capacity available → success
- Test registration when camp full → rejected

#### Blood Request Priority Test
- Test critical request priority = 1
- Test request fulfillment order by priority

---

## Future Enhancements

- **Mobile Application:** Android/iOS app for donors
- **SMS Notifications:** Integration with SMS gateway
- **Blood Type Compatibility:** Automatic cross-matching
- **Geo-location:** Find nearest camps and blood banks
- **Gamification:** Badges and rewards for regular donors
- **AI/ML:** Predict blood demand based on historical data
- **Blockchain:** Secure and transparent donation tracking
- **Blood Donation Camps Map:** Interactive map view

---

## Challenges Faced and Solutions

### Challenge 1: Managing Blood Expiry
**Problem:** Blood expires after 42 days from collection  
**Solution:** Implemented scheduled job using Spring @Scheduled to check expiry daily and send alerts

### Challenge 2: Request Prioritization
**Problem:** Multiple hospitals requesting same blood type  
**Solution:** Implemented priority-based queue using Strategy pattern with severity levels

### Challenge 3: Camp Capacity Management
**Problem:** Overbooking of camps  
**Solution:** Implemented transactional registration with pessimistic locking to prevent race conditions

### Challenge 4: Real-time Stock Updates
**Problem:** Inventory not updating immediately after donation  
**Solution:** Used Spring Events for asynchronous inventory updates after donation processing

---

## ## Conclusion

The Blood Donation and Management System successfully addresses the key challenges in blood donation management through a well-architected, scalable solution. By implementing MVC architecture, SOLID principles, and appropriate design patterns, the system provides:

- Efficient camp management for organisers
- Easy registration and donation tracking for donors
- Quick blood request processing for hospitals
- Comprehensive inventory management with alerts

The system demonstrates effective use of Object-Oriented Analysis and Design principles, with clear separation of concerns, extensibility, and maintainability. The Spring Boot framework provides a solid foundation for production deployment, while the modular design allows for future enhancements.

Each team member contributed equally to the project, owning complete use cases from design to implementation, ensuring deep understanding of the system architecture and design patterns.

---

## References

1. Spring Framework Documentation - https://spring.io/projects/spring-framework
2. Spring Boot Documentation - https://spring.io/projects/spring-boot
3. Design Patterns: Elements of Reusable Object-Oriented Software - Gang of Four
4. Head First Design Patterns - Freeman & Freeman
5. Clean Code - Robert C. Martin
6. UML Distilled - Martin Fowler
7. Java Persistence with Hibernate - Bauer & King

---

## Appendix

### A. API Endpoints Documentation

#### Camp Management APIs
- `POST /api/camps` - Create new camp
- `GET /api/camps` - Get all camps
- `GET /api/camps/{id}` - Get camp by ID
- `PUT /api/camps/{id}` - Update camp
- `DELETE /api/camps/{id}` - Cancel camp
- `GET /api/camps/{id}/registrations` - Get camp registrations

#### Donor Management APIs
- `POST /api/donors` - Register new donor
- `GET /api/donors/{id}` - Get donor details
- `GET /api/donors/{id}/eligibility` - Check eligibility
- `POST /api/donors/{id}/register-camp` - Register for camp
- `GET /api/donors/{id}/donations` - Get donation history

#### Hospital Management APIs
- `POST /api/hospitals` - Register hospital
- `POST /api/hospitals/{id}/requests` - Create blood request
- `GET /api/hospitals/{id}/requests` - Get all requests
- `PUT /api/requests/{id}/status` - Update request status

#### Inventory Management APIs
- `GET /api/inventory/stock` - Get all blood stock
- `GET /api/inventory/stock/{bloodType}` - Get stock by type
- `POST /api/inventory/stock` - Add blood stock
- `GET /api/inventory/alerts` - Get low stock alerts
- `DELETE /api/inventory/stock/expired` - Remove expired stock

### B. Database Schema Diagram

*(Refer to separate ER diagram file)*

### C. Deployment Instructions

*(To be added in final implementation)*

---

**Document Version:** 1.0  
**Last Updated:** February 10, 2026  
**Prepared By:** [Team Members]  
**Course:** UE23CS352B - Object Oriented Analysis & Design  
**Institution:** PES University