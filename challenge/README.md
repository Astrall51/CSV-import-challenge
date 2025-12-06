# CSV Coding Challenge

A Spring Boot application designed to process heterogeneous file formats (CSV, Fixed-Width) from multiple sources (REST API & Filesystem).

The solution implements the **Strategy Pattern** to decouple file parsing logic from business rules, ensuring scalability and maintainability.

## Tech Stack & Upgrades
- **Java:** 25 (LTS)
- **Framework:** Spring Boot 4.0.0
- **Build Tool:** Maven
- **Database:** H2 (Embedded) with Spring Data JPA
- **Security:** Spring Security (Custom API Key Filter)
- **CSV Processing:** OpenCSV 5.12.0
- **Testing:** JUnit 5, Mockito, Spring Boot Test

## Key Features

### 1. Multi-Channel Ingestion
- **REST API:** Upload files manually via HTTP `POST`.
- **Scheduled Tasks:** Automatically scans a local directory for files based on Cron expressions.
    - **Daily Job:** Processes `CUSTCOMP` and `OUTPH` files.
    - **Weekly Job:** Processes `ZTPSPF` files.

### 2. Architecture & Patterns
- **Strategy Pattern:** `ImportStrategy` interface enables adding new file formats (e.g., XML, JSON) without modifying the core service.
- **Idempotency:** Prevents duplicate processing of the same file using an `ImportLog` audit table.
- **Global Error Handling:** `@ControllerAdvice` ensures consistent JSON error responses (RFC 7807 style).
- **Separation of Concerns:** The core logic operates on `InputStream`, making it independent of the source (Web vs. Disk).

### 3. Security
- The API is protected by a custom **API Key Filter**.
- Requests must include the `X-API-KEY` header.
- CSRF is disabled for stateless REST API usage.

## Configuration (`application.properties`)

You can configure the application behavior without recompiling code:

```properties
# Security
app.security.api-key=S3cretKey123!

# File System Paths for Scheduled Tasks
app.import.input-dir=./import_input
app.import.archive-dir=./import_archive

# Scheduling (Cron Expressions)
app.schedule.daily=0 0 0 * * * # Daily at 12:00 AM
app.schedule.weekly=0 0 0 * * SUN # Sundays at 12:00 AM