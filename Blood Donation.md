# Blood donation and management system

UE23CS352B, Object Oriented Analysis & Design. Healthcare mini project, Java web application.

Original team: Satwik R Bankapur (PES2UG23CS539), Sathwik HS (PES2UG23CS536), Prajwal K (PES234UG23CS818), Sudarshan JN (PES234UG23CS825).

Repository: https://github.com/hs-sathwikhs/Blood_Donation_System

## Problem and scope

Donation camps, donor visits and hospital demand need a shared record. This app connects four roles around one blood bank: organisers manage camps, donors reserve places and record donations, hospitals request blood, and the admin counts stock and fulfills requests.

The [course guidelines](docs/Mini%20Project%20Guidelines.pdf) call for Java, MVC, persistent data, four major and four minor use cases, one use case and class model, four activity and state models, and design principles and patterns across the team. These notes describe the current code. The original submission is already completed.

| Module | Major use case | Minor use case |
| --- | --- | --- |
| Camps | Create, edit, publish, start and complete camps | Search by name or location, date and state |
| Donors | Reserve camp places and record donations | Check age and donation waiting period |
| Hospitals | Request blood and follow status | Choose severity to order the request queue |
| Inventory | Add and edit batches, fulfill requests | Check expiry and low stock, remove expired batches |

Camp changes require the owning organiser. Donors and hospitals can only use their own records. Admin writes are protected on the server. Published camps accept eligible donors until capacity is reached. Cancelling a registration releases its place; cancelling a camp cancels its active registrations. Donations require an active camp today and a registered donor. A completed camp closes donations.

Eligibility uses ages 18 to 65 and at least 90 days since the last donation. A donation adds 0.5 to 1 project unit; stock expires after 42 days. Requests use the exact blood group. These are simplified project rules.

## MVC and data

Thymeleaf renders the login and shared role dashboard. Plain JavaScript submits forms and refreshes records every 20 seconds. Controllers check access and validate input. Services apply camp, donor and inventory rules. Spring Data JPA stores data in file-based H2, with separate demo and test databases.

```mermaid
flowchart LR
    view[Thymeleaf and JavaScript] --> controller[Controllers]
    controller --> access[Role and owner checks]
    controller --> service[Services and rules]
    service --> repo[JPA repositories]
    repo --> db[(H2 database)]
```

Registration locks the camp before counting places. Donation recording locks the donor and camp, then updates the donation, donor, registration and stock in one transaction. Fulfillment locks the request and matching stock, checks the full amount before deducting, and changes Pending to Fulfilled. A second fulfillment is rejected. Version fields on requests and stock detect competing edits.

Passwords use BCrypt. JWTs contain account ID and role, expire after two hours and stay in session storage. The default signing key changes on restart; JWT_SECRET can supply a persistent key. The H2 console is disabled and the app listens on localhost by default.

## Patterns and principles

| Pattern | Category | Working example |
| --- | --- | --- |
| Simple factory | Creational | BloodRequestFactory creates Routine, Urgent or Critical subclasses and sets their priority |
| Adapter | Structural | EmailServiceAdapter implements NotificationService using the logging API for local delivery simulation |
| Observer | Behavioral | CampPublishedEvent is handled by CampEventListener asynchronously after publication commits |
| Strategy | Behavioral | AllocationStrategy chooses stock by date added (FIFO) or nearest expiry (Priority) for fulfillment |

Spring supplies dependency injection, singleton component scope and MVC. The four application patterns are separate from those framework facilities. Camp notices are logged; no email or SMS is sent.

| Principle | Example |
| --- | --- |
| Single responsibility | EligibilityCriteria checks donor rules; CampService controls camp lifecycle |
| Open/closed | Another allocation order can implement AllocationStrategy without changing the fulfillment loop |
| Liskov substitution | Every request subclass works as BloodRequest and supplies its computed priority |
| Dependency inversion | The camp listener depends on NotificationService; stock selection uses AllocationStrategy |

## Use case model

An editable UML use case model is in [BloodDonationSystem_UseCaseDiagram.drawio](docs/BloodDonationSystem_UseCaseDiagram.drawio). The view below shows the role associations and included checks.

```mermaid
flowchart LR
    donor[Donor]
    organiser[Organiser]
    hospital[Hospital]
    admin[Admin]
    subgraph app[Blood donation system]
        login([Sign in])
        search([Search camps])
        camp([Manage camp lifecycle])
        register([Reserve or cancel a place])
        eligible([Check eligibility and capacity])
        donate([Record donation and view history])
        request([Request blood and follow status])
        severity([Choose severity])
        stock([Manage stock batches])
        fulfill([Fulfill request])
        expiry([Check usable stock and expiry])
    end
    donor --> login
    donor --> search
    donor --> register
    donor --> donate
    organiser --> login
    organiser --> camp
    hospital --> login
    hospital --> request
    admin --> login
    admin --> stock
    admin --> fulfill
    register -. includes .-> eligible
    donate -. includes .-> eligible
    request -. includes .-> severity
    fulfill -. includes .-> expiry
    stock -. includes .-> expiry
```

## Class model

IDs are UUIDs. Request subclasses share blood_request using a severity discriminator. Registration has a unique donor/camp pair. Associations record ownership and links between visits, requests and stock. No separate reports or allocation-history entities are used.

```mermaid
classDiagram
    class User {
        <<abstract>>
        getName()
        getEmail()
        getPassword()
    }
    class Donator {
        UUID donatorId
        int age
        String bloodType
        LocalDate lastDonationDate
    }
    class Camp {
        UUID campId
        LocalDate date
        int capacity
        String status
    }
    class Registration {
        UUID registrationId
        String status
    }
    class Donation {
        UUID donationId
        double quantity
        LocalDate donationDate
    }
    class BloodRequest {
        <<abstract>>
        UUID requestId
        double quantity
        String status
        int getComputedPriority()
    }
    class BloodStock {
        UUID stockId
        String bloodType
        double quantity
        LocalDate expiryDate
        Long version
    }
    class AllocationStrategy {
        <<interface>>
        allocate(bloodType, currentDate, repository)
    }
    User <|-- Donator
    User <|-- Organiser
    User <|-- Hospital
    User <|-- Admin
    Organiser "1" --> "0..*" Camp : owns
    Donator "1" --> "0..*" Registration
    Camp "1" --> "0..*" Registration
    Donator "1" --> "0..*" Donation
    Camp "1" --> "0..*" Donation
    Hospital "1" --> "0..*" BloodRequest
    BloodRequest <|-- RoutineBloodRequest
    BloodRequest <|-- UrgentBloodRequest
    BloodRequest <|-- CriticalBloodRequest
    BloodRequestFactory ..> BloodRequest : creates
    Inventory "1" --> "0..*" BloodStock
    AllocationStrategy <|.. FIFOAllocationStrategy
    AllocationStrategy <|.. PriorityAllocationStrategy
    InventoryManager ..> AllocationStrategy
    InventoryService ..> InventoryManager
    DonatorService ..> EligibilityCriteria
    CampService ..> CampPublishedEvent
    CampEventListener ..> CampPublishedEvent : observes
    CampEventListener ..> NotificationService
    NotificationService <|.. EmailServiceAdapter
```

## Activity models

### Camp management

```mermaid
flowchart TD
    start((Start)) --> auth[Sign in as organiser]
    auth --> form[Enter camp details]
    form --> valid{Valid date and details?}
    valid -- No --> form
    valid -- Yes --> save[Save Draft with owner]
    save --> publish[Publish camp]
    publish --> event[Commit and raise camp notice]
    event --> date{Camp date is today?}
    date -- No --> wait[Wait until camp date]
    wait --> date
    date -- Yes --> active[Start camp]
    active --> visits[View registrations and accept donations]
    visits --> complete[Complete camp]
    complete --> finish((End))
```

### Donor visit

```mermaid
flowchart TD
    start((Start)) --> search[Sign in and choose camp]
    search --> lock[Lock camp]
    lock --> valid{Eligible, open and space available?}
    valid -- No --> error[Show reason]
    error --> search
    valid -- Yes --> reg[Save Registered place]
    reg --> cancel{Cancel place?}
    cancel -- Yes --> release[Set Cancelled and release capacity]
    cancel -- No --> visit[Attend active camp today]
    visit --> donation[Submit 0.5 to 1 unit]
    donation --> check{Registered and still eligible?}
    check -- No --> error
    check -- Yes --> tx[Save donation, last date, Donated registration and stock]
    tx --> history[Show history and waiting period]
    release --> finish((End))
    history --> finish
```

### Hospital request

```mermaid
flowchart TD
    start((Start)) --> login[Sign in as hospital]
    login --> form[Enter blood group, units and severity]
    form --> valid{Fields valid and account matches?}
    valid -- No --> form
    valid -- Yes --> factory[Factory creates request subclass]
    factory --> pending[Save Pending request and priority]
    pending --> queue[Admin queue orders severity then date]
    queue --> enough{Enough usable stock?}
    enough -- No --> wait[Keep Pending until stock arrives]
    wait --> enough
    enough -- Yes --> fulfill[Admin confirms fulfillment]
    fulfill --> status[Hospital sees Fulfilled on refresh]
    status --> finish((End))
```

### Inventory management

```mermaid
flowchart TD
    start((Start)) --> login[Sign in as admin]
    login --> counts[Read usable totals and expiry]
    counts --> action{Choose action}
    action -- Intake --> intake[Validate units, save batch with 42-day expiry]
    action -- Correct count --> update[Lock batch and update quantity]
    action -- Remove expired --> remove[Delete batches expired before today]
    action -- Fulfill --> lock[Lock Pending request and matching usable stock]
    lock --> enough{Total covers whole request?}
    enough -- No --> error[Reject without deducting]
    enough -- Yes --> order[Choose FIFO or nearest expiry strategy]
    order --> deduct[Deduct batches and set Fulfilled in one transaction]
    intake --> refresh[Refresh totals and queue]
    update --> refresh
    remove --> refresh
    error --> refresh
    deduct --> refresh
    refresh --> finish((End))
```

## State models

### Camp

```mermaid
stateDiagram-v2
    [*] --> Draft
    Draft --> Scheduled: publish
    Scheduled --> Active: start on camp date
    Active --> Completed: complete
    Draft --> Cancelled: cancel
    Scheduled --> Cancelled: cancel
    Active --> Cancelled: cancel
    Completed --> [*]
    Cancelled --> [*]
```

### Donor eligibility

Eligibility is computed from age and last donation date on each check, not a scheduled background update.

```mermaid
stateDiagram-v2
    [*] --> Check
    Check --> Eligible: age 18 to 65 and waiting period passed
    Check --> Waiting: donation less than 90 days ago
    Check --> AgeIneligible: age outside allowed range
    Eligible --> Waiting: record donation
    Waiting --> Eligible: 90 days passed at next check
    Eligible --> AgeIneligible: age outside allowed range
    AgeIneligible --> [*]
```

### Blood request

```mermaid
stateDiagram-v2
    [*] --> Pending: create request
    Pending --> Pending: insufficient usable stock
    Pending --> Fulfilled: admin deducts full quantity
    Fulfilled --> [*]
```

### Stock batch

These states describe date and quantity, not a stored status column.

```mermaid
stateDiagram-v2
    [*] --> Usable: intake or donation
    Usable --> Usable: partial allocation or count correction
    Usable --> Depleted: quantity reaches zero
    Depleted --> Usable: counted stock correction
    Usable --> Expired: expiry date before today
    Depleted --> Expired: expiry date before today
    Expired --> Removed: admin removes expired batches
    Removed --> [*]
```

## Team context

Most implementation and the later repo polish were handled by Sathwik HS. The team split modules for the original presentation. The remaining member-to-module allocation was not recorded, so these notes do not assign individual implementation credit without that information. The module table is a useful way to split a walkthrough.

## Screenshots

These come from the running app with sample inputs and results. The current UI is dark; the original guidelines requested white backgrounds for submission screenshots. No new submission report is being prepared.

![Login and open camps](docs/screenshots/login.png)

![Donation recorded, eligibility and history](docs/screenshots/donor.png)

![Camp details and lifecycle](docs/screenshots/organiser.png)

![Hospital request submitted and history](docs/screenshots/hospital.png)

![Stock counts, pending requests and intake](docs/screenshots/inventory.png)

![Responsive hospital dashboard](docs/screenshots/hospitalMobile.png)

## Validation and limits

Run ./mvnw test for startup, age eligibility, role and ownership restrictions, malformed input and JWTs, donation updates, duplicate registration, cancellation, camp rules, concurrent capacity limits, stock expiry, allocation order and repeat fulfillment. Run ./mvnw package to produce the runnable jar.

Chrome checks cover the four role workflows, desktop and mobile layouts, form feedback, timed refresh, offline recovery and invalid sessions. Inputs are labelled and the UI has keyboard focus, native confirmation dialogs and reduced-motion styles.

No maps, certificates, compatibility matching, analytics, payments or real messaging are implemented. There is one inventory location. Public organiser and hospital signup are suitable for the local demo; there is no account approval process. H2 and simple queries keep the project easy to run and explain.
