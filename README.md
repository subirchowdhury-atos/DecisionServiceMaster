# Decision Service Master

A Spring Boot application for automated loan decision processing based on credit scores and mortgage information. This system evaluates loan applications against configurable business rules for different states and counties.

## Overview

The Decision Service Master processes loan applications by:
1. Validating applicant and property information
2. Fetching credit reports and mortgage data from external services
3. Applying state and county-specific business rules
4. Generating eligibility decisions with funding options

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.0**
- **PostgreSQL** (production)
- **PostgreSQL** (development/testing using docker)
- **Maven** for dependency management
- **Docker** for containerization

## Project Structure

```
src/main/java/com/decisionservicemaster/
├── config/                      # Spring configuration classes
├── controller/                 # REST API controllers
├── domain/entity/              # JPA entities
├── dto/                        # Data Transfer Objects (Ex- API request/response)
├── repository/                 # JPA repositories
├── service/                    # Business logic services
│   ├── report/                 # Report fetching services
│   ├── rule/                   # Business rule implementations
│   ├── parser/                 # Data parsing utilities
│   ├── Processor.java
│   ├── RequestBuilder.java
│   └── EncryptionService.java
└── DecisionServiceMasterApplication.java

src/main/resources/
├── rules/                      # Business rule configurations
├── sample_data/                # Mock data for testing
├── application.yml             # Default configuration
├── application-dev.yml         # Development profile
├── application-local.yml       # Local testing profile for docker
├── application-test.yml        # Test profile
└── application-production.yml  # Production profile
```

## Prerequisites (TODO: change as per client request later)

- Java 21+
- Maven 3.8+
- Docker & Docker Compose (for running with PostgreSQL)
- PostgreSQL 16+ (if running without Docker)

## Configuration

### Application Profiles

- **dev** - Development with H2 in-memory database
- **test** - Testing with H2
- **local** - Local Docker environment with PostgreSQL
- **prod** - Production configuration

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd DecisionServiceMaster
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run with Docker 

```bash
# Option 1: Start PostgreSQL and the application
docker-compose build
docker-compose up

# Option 2:Start only PostgreSQL container and run app with Maven
docker-compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=local

# View logs
docker-compose logs -f app

# Stop everything
docker-compose down
```

The API will be available at: `http://localhost:8080`

### 4. Run Locally (without Docker)

```bash
# Using development profile (postgress database)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Or with PostgreSQL
export SPRING_PROFILES_ACTIVE=local
mvn spring-boot:run
```

## API Documentation

### Authentication

All API endpoints require an API token in the header:

```bash
API-TOKEN: your-api-token-here
```

### Create Decision

**Endpoint:** `POST /api/v1/decisions`

**Request Body:**
```json
{
  "applicationId": 123,
  "firstName": "John",
  "lastName": "Doe",
  "ssn": "123456789",
  "income": 50000,
  "incomeType": "salary",
  "requestedLoanAmount": 100000,
  "address": {
    "street": "212 Encounter Bay",
    "unitNumber": "123",
    "city": "San Francisco",
    "zip": "94102",
    "state": "California",
    "county": "San Francisco"
  }
}
```

**Response:**
```json
{
  "application_id": 123,
  "address": {
    "street": "212 Encounter Bay",
    "city": "San Francisco",
    "state": "California",
    "county": "San Francisco",
    "zip": "94102"
  },
  "applicant": {
    "first_name": "John",
    "last_name": "Doe",
    "income": 50000.0,
    "requested_loan_amount": 100000.0
  },
  "final_decision": "eligible",
  "decision": [
    {
      "rule_name": "mortgage_rule",
      "decision": "eligible",
      "message": "The outstanding mortgage loan on the applicants property is checked in relation with his income."
    },
    {
      "rule_name": "credit_rule",
      "decision": "eligible",
      "message": "The credit score of applicant is checked"
    }
  ],
  "funding_options": [
    {
      "years": 5,
      "interest_rate": 6,
      "emi": 100
    },
    {
      "years": 10,
      "interest_rate": 6,
      "emi": 60
    }
  ]
}
```

## Testing

### Run All Tests

```bash
mvn test
```

## Database

### Schema Overview

- **decision_requests** - Loan application requests
- **applicants** - Applicant information
- **addresses** - Property addresses
- **decisions** - Individual rule decisions
- **credit_reports** - Credit score data
- **mortgage_reports** - Mortgage information

## Development

### Adding a New Rule

1. Create YAML configuration in `src/main/resources/rules/`
2. Implement rule class extending `BaseRule`
3. Add rule to `RULE_SET` in `Processor.java`
4. Update report services if external data is needed

## Deployment

### Production Checklist

- [ ] Set secure `API_TOKEN` environment variable
- [ ] Configure production database credentials
- [ ] Set `ENCRYPTION_KEY` for SSN encryption
- [ ] Review and update business rule configurations
- [ ] Enable SSL/TLS
- [ ] Configure proper logging levels
- [ ] Set up monitoring and alerting
