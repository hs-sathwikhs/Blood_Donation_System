# Blood donation system

An OOAD college project for organising donation camps, keeping donor records and managing hospital blood requests. Built with Java 17, Spring Boot, Thymeleaf, plain JavaScript and H2.

## Run locally

Install JDK 17 or newer. The Maven wrapper downloads Maven and dependencies on the first run.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Open http://localhost:8080. On Windows, use `mvnw.cmd` instead of `./mvnw`.

| Role | Email | Password |
| --- | --- | --- |
| Donor | aarav.patel@gmail.com | pass123 |
| Organiser | priya.sharma@redcross.in | password123 |
| Hospital | blood@manipal-blr.org | hosp123 |
| Admin | admin@bloodbank.org | admin123 |

These are local sample accounts, seeded only with the demo profile. More accounts are in [demo-credentials.txt](demo-credentials.txt). Demo data stays in `data/demoDb.mv.db`; stop the app and delete that file to start a fresh demo with current camp dates.

Run without the demo profile for an empty database: `./mvnw spring-boot:run`. Donors, organisers and hospitals can register from the login page. Admin accounts are supplied by the demo, there is no public admin registration.

## What it does

- Create, edit, publish, start and complete camps, with owner checks.
- Search camps, reserve or cancel a place, check eligibility and record donations.
- Send hospital requests and track pending or fulfilled status.
- Count usable stock, add or edit batches, remove expired stock and fulfill requests by date added or expiry date.

## How it works

Controllers handle requests, services apply the rules and JPA repositories persist records in H2. Donations update the donor, registration and stock together. Fulfillment locks the request and stock so it cannot deduct twice. The dashboards refresh every 20 seconds and show low stock from actual counts.

Passwords use BCrypt. Two-hour JWTs are kept in the browser tab's session storage. Restarting the app ends sessions unless `JWT_SECRET` is set to a base64-encoded key of at least 32 random bytes. Optional settings are shown in [.env.example](.env.example); export them in your shell, Spring does not load that file automatically. The app binds to localhost by default.

```bash
./mvnw test
./mvnw package
java -jar target/blood-donation-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
```

Blood matching uses the exact group. Eligibility is the project's age and 90-day rule, and camp notifications are logged locally. [Course notes](Blood%20Donation.md) cover the four modules, UML diagrams and design patterns. [Screenshots](docs/screenshots) show the working UI.
