# Time Deposit Refactoring Kata - Take-Home Assignment

## XA Bank Time Deposit

### Context
A junior developer implemented domain logic for a time deposit system but did not complete the API functionality. Your task is to refactor the existing codebase to implement all required functionalities based on the provided business requirements, ensuring no breaking changes occur.

### Requirements

1. **API Endpoints**:
    - Create a RESTful API endpoint to update the balances of all time deposits in the database.
    - Create a RESTful API endpoint to retrieve all time deposits.
        - The GET endpoint should return a list of all time deposits with the following schema:
            - `id`
            - `planType`
            - `balance`
            - `days`
            - `withdrawals`

2. **Database Setup**:
    - Store all time deposit plans in a database.
    - Define the following tables:
        - `timeDeposits`:
            - `id`: Integer (primary key)
            - `planType`: String (required)
            - `days`: Integer (required)
            - `balance`: Decimal (required)
        - `withdrawals`:
            - `id`: Integer (primary key)
            - `timeDepositId`: Integer (foreign key, required)
            - `amount`: Decimal (required)
            - `date`: Date (required)

3. **Interest Calculation**:
    - Implement logic to calculate monthly interest based on the plan type:
        - **Basic Plan**: 1% interest
        - **Student Plan**: 3% interest (no interest after 1 year)
        - **Premium Plan**: 5% interest (interest starts after 45 days)
    - No interest is applied for the first 30 days for any existing plans.

4. **Refactoring Constraints**:
    - Do not introduce breaking changes to the shared `TimeDeposit` class or modify the `updateBalance` method signature.
    - Ensure the design is extensible to accommodate future complexities in interest calculations.

5. **Code Quality**:
    - Adhere to SOLID principles, design patterns, and clean code practices where applicable.

### Important Guidelines
- The existing `TimeDepositCalculator.updateBalance` method is functioning correctly. Ensure its behavior remains unchanged after refactoring.
- The final solution must include **exactly two API endpoints**. Do not develop additional endpoints.
- **Do not** create a pull request or a new branch in the ikigai-digital repository. Instead, fork the repository into your own GitHub repository and develop the solution there.
- Handling invalid input or exceptions is not required.
- Use any tools, frameworks, or libraries you find suitable.
- In case of ambiguity, make logical assumptions and justify them in code comments.

### Preferred Stack
- Use an OpenAPI Swagger contract.
- Embrace Hexagonal Architecture.
- Follow atomic commit practices.
- Utilize testcontainers.

### Submission Instructions
- Provide clear instructions on how to trigger the endpoints using the Swagger contract.
- Email the link to your public GitHub repository.

---

## 🚀 How to Run the Project

### Prerequisites

- **Java 21** (Required - Do not use other versions)
- **Maven 3.8+**
- **Docker** (for running PostgreSQL database)
- **Docker Compose** (optional, for easier setup)

### Technology Stack

- **Spring Boot 3.2.2**
- **Java 21**
- **PostgreSQL 15**
- **OpenAPI 3.0.3** (API-First Development)
- **MapStruct** (DTO Mapping)
- **Liquibase** (Database Migrations)
- **Lombok** (Boilerplate Reduction)
- **Testcontainers** (Integration Testing)

### Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters) with clear separation of concerns:

- `domain` - Core business logic and domain models
- `application` - REST controllers and DTOs
- `infrastructure` - Database persistence, external configurations

### Running the Application

#### Option 1: Using Docker Compose (Recommended)

```bash
# Navigate to the Java project directory
cd java

# Start the application and database
docker-compose up

# The application will be available at http://localhost:8080
```

#### Option 2: Run with Local Database

```bash
# Start PostgreSQL database (using Docker)
docker run -d \
  --name time-deposit-db \
  -e POSTGRES_DB=timedeposit \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -p 5433:5432 \
  postgres:15-alpine

# Navigate to the Java project directory
cd java

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/time-deposit-kata-1.0-SNAPSHOT.jar
```

#### Option 3: Run in IDE

1. Import the project as a Maven project in IntelliJ IDEA or Eclipse
2. Ensure Java 21 is configured as the project SDK
3. Run the `TimeDepositApplication` class
4. The application will start on port 8080

### API Documentation & Testing

#### Swagger UI

Once the application is running, access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

#### API Endpoints

##### 1. Update Time Deposit Balances

- **Endpoint**: `POST /api/time-deposits/update-balances`
- **Description**: Calculates and applies monthly interest to all time deposits
- **Response**: Returns the number of updated deposits and timestamp

**Example using cURL:**

```bash
curl -X POST http://localhost:8080/api/time-deposits/update-balances \
  -H "Content-Type: application/json"
```

##### 2. Get All Time Deposits

- **Endpoint**: `GET /api/time-deposits`
- **Description**: Retrieves all time deposits with pagination support
- **Query Parameters**:
    - `page` (default: 0) - Page number (0-based)
    - `size` (default: 20) - Number of items per page
    - `sort` (default: "id,asc") - Sort field and direction

**Example using cURL:**

```bash
# Get all deposits (default pagination)
curl -X GET http://localhost:8080/api/time-deposits

# Get with custom pagination
curl -X GET "http://localhost:8080/api/time-deposits?page=0&size=10&sort=balance,desc"
```

### Database Configuration

The application uses PostgreSQL with the following default configuration:

- **Database**: timedeposit
- **Username**: admin
- **Password**: admin
- **Port**: 5433 (mapped from container's 5432)

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TimeDepositControllerTest

# Run with coverage report
mvn clean test jacoco:report
```

### Key Implementation Features

#### Strategy Pattern for Interest Calculation

The project uses the Strategy Pattern to handle different interest calculation rules:

- `BasicPlanInterestStrategy` - 1% annual interest after 30 days
- `StudentPlanInterestStrategy` - 3% annual interest (30-365 days only)
- `PremiumPlanInterestStrategy` - 5% annual interest after 45 days

#### API-First Development

The project follows API-first development principles:

- OpenAPI specification defined in `/src/main/resources/openapi/time-deposit-api.yaml`
- Controllers implement generated API interfaces
- DTOs are generated from the OpenAPI specification

#### Database Migrations

Liquibase manages all database changes:

- Migration files in `/src/main/resources/db/changelog/`
- Automatic schema creation and sample data insertion
- Version-controlled database changes

### Troubleshooting

#### IDE Issues

If you see red underlines in the IDE:

1. **IntelliJ IDEA**:
    - Right-click `pom.xml` → Maven → Reload Project
    - File → Invalidate Caches → Invalidate and Restart
2. **Eclipse**:
    - Right-click project → Maven → Update Project

#### Port Conflicts

If port 8080 or 5433 is already in use:

- Change the port in `application.yml`
- Or stop the conflicting service

#### Database Connection Issues

Ensure PostgreSQL is running and accessible:

```bash
# Check if database container is running
docker ps | grep time-deposit-db

# Check database logs
docker logs time-deposit-db
```

### Project Structure

```
java/
├── src/
│   ├── main/
│   │   ├── java/org/ikigaidigital/
│   │   │   ├── application/       # REST layer
│   │   │   ├── domain/           # Business logic
│   │   │   └── infrastructure/   # External concerns
│   │   └── resources/
│   │       ├── openapi/          # API specification
│   │       └── db/changelog/     # Database migrations
│   └── test/                     # Test files
├── docker-compose.yml            # Docker composition
├── Dockerfile                    # Container definition
└── pom.xml                      # Maven configuration
```

### Contact & Support

For any questions or issues, please create an issue in the GitHub repository.