# TuLlave Recharge API

REST API for managing digital TuLlave card recharges, developed with Java 21, Spring Boot, and PostgreSQL.

The project allows creating, retrieving, and deleting recharges, including data validation, pagination, card number filtering, and centralized error handling.

---

## Summary

The API exposes the following main endpoints:

| Method   | Endpoint                 | Description                         |
| -------- | ------------------------ | ----------------------------------- |
| `POST`   | `/api/v1/recharges`      | Creates a new recharge              |
| `GET`    | `/api/v1/getRecharges`   | Retrieves recharges with pagination |
| `DELETE` | `/api/v1/recharges/{id}` | Deletes a recharge                  |

The recharge query endpoint also supports filtering by card number.

### Business Rules

* The card number must contain exactly 16 numeric digits.
* The recharge amount must be between `$2,000` and `$200,000` COP.
* Available payment methods are:

  * `PSE`
  * `NEQUI`
  * `DAVIPLATA`
  * `CREDIT_CARD`
* Pagination uses zero-based page numbers.
* The maximum page size is 100 records.

The API also includes:

* Validation using Jakarta Bean Validation.
* DTOs for request and response data.
* Centralized exception handling.
* Logging using SLF4J.
* Automated tests using JUnit 5, Mockito, and Spring MVC Test.
* OpenAPI/Swagger documentation.
* Test execution during the Docker build process.

---

## Dependencies

### Runtime

* Java 21
* Spring Boot 3.5.16
* Spring Web
* Spring Data JPA
* PostgreSQL
* Jakarta Validation

### Testing

* JUnit 5
* Mockito
* Spring Boot Test
* Spring MVC Test

### Documentation

* Springdoc OpenAPI
* Swagger UI

### Infrastructure

* Maven 3.9+
* Docker
* Docker Compose

---

## How to Run Locally

The project can be run in two ways: directly with Maven or using Docker Compose.

### Option 1 — Docker Compose

This is the recommended option because it starts both the application and PostgreSQL using the configuration defined in `docker-compose.yml`.

Run:

```bash
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
```

PostgreSQL will be available from the host at:

```text
localhost:5433
```

Port `5433` is used to avoid conflicts with other PostgreSQL instances that may already be running on port `5432`.

To stop the services:

```bash
docker compose down
```

To stop the services and remove the associated volumes:

```bash
docker compose down -v
```

### Option 2 — Local with Maven

To run the application directly on the local machine, the following are required:

* Java 21
* Maven 3.9+
* PostgreSQL running locally

The default configuration expects PostgreSQL at:

```text
localhost:5432
```

with:

```text
Database: tullave
Username: postgres
Password: postgres
```

The database connection can also be configured using environment variables:

```bash
export DATASOURCE_URL=jdbc:postgresql://localhost:5432/tullave
export DATASOURCE_USERNAME=postgres
export DATASOURCE_PASSWORD=postgres
```

Create the database if it does not already exist:

```sql
CREATE DATABASE tullave;
```

Then run:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

---

## Running the Tests

To run all automated tests:

```bash
mvn clean test
```

The test suite mainly covers:

* Recharge creation.
* Recharge retrieval.
* Filtering by card number.
* Recharge deletion.
* Card number validation.
* Recharge amount validation.
* Pagination parameter validation.
* Payment method validation.
* Handling non-existing recharges.
* Centralized error handling.

Tests are also executed during the Docker build before the final runtime image is created.

---

## API Documentation

The API includes OpenAPI documentation and Swagger UI.

With the application running, open:

```text
http://localhost:8080/swagger-ui/index.html
```

Swagger provides an interactive view of the available endpoints, request parameters, request/response models, and HTTP response codes.

---

## Postman

The project includes a Postman collection for testing the main successful and error scenarios.

The collection uses the following variable:

```text
{{baseUrl}}
```

with the default value:

```text
http://localhost:8080
```

The collection covers:

* Successful recharge creation.
* Invalid card number validation.
* Invalid amount validation.
* Invalid payment method validation.
* Paginated recharge retrieval.
* Card number filtering.
* Invalid pagination parameters.
* Successful recharge deletion.
* Deletion of a non-existing recharge.

---

## How to Upload to Production

The application is prepared to be packaged as a Docker image and run on a server or any container-compatible platform.

### 1. Build the Docker image

```bash
docker build -t tullave-recharge:latest .
```

### 2. Push the image to a container registry

For example, using a registry such as Docker Hub:

```bash
docker tag tullave-recharge:latest <registry-user>/tullave-recharge:latest

docker push <registry-user>/tullave-recharge:latest
```

### 3. Deploy the image

On the production server, pull the image:

```bash
docker pull <registry-user>/tullave-recharge:latest
```

Then run the container providing the PostgreSQL connection variables:

```bash
docker run -d \
  --name tullave-recharge \
  -p 8080:8080 \
  -e DATASOURCE_URL=<production-database-url> \
  -e DATASOURCE_USERNAME=<production-database-user> \
  -e DATASOURCE_PASSWORD=<production-database-password> \
  <registry-user>/tullave-recharge:latest
```

In a real production environment, credentials and other sensitive variables should be managed using secrets or environment variables provided by the infrastructure platform instead of being stored directly in the repository.

The included `docker-compose.yml` is primarily intended for local development and testing.

---

## Project Architecture

The project follows a layered architecture to separate responsibilities:

```text
src/main/java/com/tullave/recharge/

├── config/
│   └── OpenApiConfig.java
│
├── controller/
│   └── RechargeController.java
│
├── dto/
│   ├── RechargeRequest.java
│   └── RechargeResponse.java
│
├── entity/
│   └── Recharge.java
│
├── enums/
│   └── PaymentMethod.java
│
├── exception/
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── RechargeNotFoundException.java
│
├── repository/
│   └── RechargeRepository.java
│
├── service/
│   └── RechargeService.java
│
└── TuLlaveRechargeApplication.java
```

### Controller

`RechargeController` is responsible for receiving HTTP requests, validating input parameters, and returning the corresponding responses.

It does not contain business logic or direct database access.

### Service

`RechargeService` contains the business logic for recharge operations.

It coordinates operations between the DTOs and the repository.

### Repository

`RechargeRepository` uses Spring Data JPA to access PostgreSQL.

The project uses `JpaRepository` for CRUD operations and pagination support.

### Entity

`Recharge` represents the entity persisted in PostgreSQL.

The creation timestamp is automatically generated before the entity is persisted.

### DTOs

DTOs are used to avoid exposing the JPA entity directly through the API.

`RechargeRequest` represents the information required to create a recharge and contains the corresponding validation rules.

`RechargeResponse` represents the data returned by the API.

### Exception Handling

`GlobalExceptionHandler` centralizes the handling of validation errors, missing resources, invalid requests, and unexpected errors.

`ErrorResponse` defines the standard structure returned by the API when an error occurs.

`RechargeNotFoundException` represents the specific case where a requested recharge does not exist.

### Configuration

`OpenApiConfig` configures the API's OpenAPI documentation and Swagger metadata.

### Exception Handling

`GlobalExceptionHandler` centralizes the handling of validation errors, missing resources, invalid requests, and unexpected errors.

This keeps the API error response format consistent.

---

## Technical Decisions

### Java 21

Java 21 is used as a modern LTS version compatible with Spring Boot 3.

### Spring Boot

Spring Boot provides the foundation for implementing the REST API and integrates Spring MVC, Validation, Spring Data JPA, and the testing ecosystem.

### PostgreSQL

PostgreSQL is used as the relational database for storing recharge information.

### Spring Data JPA

Spring Data JPA reduces the amount of persistence-related code and provides built-in support for CRUD operations and pagination through `Pageable`.

### DTOs

Request and response objects are separated from the JPA entity to maintain a clear separation between the persistence layer and the API contract.

### Bean Validation

Input validation is implemented using annotations such as:

* `@NotBlank`
* `@NotNull`
* `@Pattern`
* `@DecimalMin`
* `@DecimalMax`
* `@Min`
* `@Max`

This allows invalid data to be rejected before reaching the business logic.

### Centralized Exception Handling

`@RestControllerAdvice` is used to centralize exception handling and return consistent error responses.

### Pagination

Recharge queries use `Pageable` and `Page<T>` to avoid loading all records into memory.

A maximum page size of 100 records is also enforced.

### Logging

SLF4J is used to log validation errors, missing resources, and unexpected errors.

### Swagger / OpenAPI

OpenAPI documentation was added to make the API easier to explore and test during development and evaluation.

---

## Dockerfile

The `Dockerfile` uses a multi-stage build.

### Build Stage

The first stage uses Maven with Java 21:

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
```

During this stage:

1. The `pom.xml` file is copied.
2. Dependencies are downloaded.
3. The source code is copied.
4. Tests are executed.
5. The application JAR is generated.

The main command is:

```bash
mvn clean package
```

This ensures that the build stops if any test fails before the final runtime image is created.

### Runtime Stage

The second stage uses a smaller Java 21 JRE image:

```dockerfile
FROM eclipse-temurin:21-jre-alpine AS runner
```

Only the generated JAR is copied from the build stage.

This prevents Maven, source code, and other development tools from being included in the final runtime image.

The application runs using a non-root user:

```dockerfile
USER tullave:tullave
```

Finally, the container exposes port:

```text
8080
```

---

## Docker Compose

The `docker-compose.yml` defines two services:

```text
db
└── PostgreSQL 17

app
└── TuLlave Recharge API
```

### Database Service

The `db` service uses:

```text
postgres:17-alpine
```

It configures:

```text
Database: tullave
Username: postgres
Password: postgres
```

The host port `5433` is mapped to container port `5432`:

```yaml
ports:
  - "5433:5432"
```

This allows the containerized database to run without interfering with another PostgreSQL instance running locally.

A Docker volume is used to persist database data:

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

A health check using `pg_isready` is also configured.

### Application Service

The `app` service builds the application image using the `Dockerfile`.

The application waits for PostgreSQL to become healthy:

```yaml
depends_on:
  db:
    condition: service_healthy
```

Inside the Docker network, the application connects to PostgreSQL using the service name:

```text
jdbc:postgresql://db:5432/tullave
```

The application's port `8080` is exposed on the host:

```yaml
ports:
  - "8080:8080"
```

---

## Git Workflow

The project uses a branch-based workflow combined with Conventional Commits.

The main branch is:

```text
main
```

Features and changes are developed in separate branches.

Examples:

```text
feature/recharge-api
feature/docker
test/recharge-api
```

Commits follow the Conventional Commits format using prefixes such as:

```text
feat:
fix:
test:
chore:
```

Examples used during development:

```text
chore: initialize TuLlave recharge service project
```

```text
feat: implement recharge functionality with controller, service, repository, and error handling
```

```text
fix: validate pagination parameters
```

```text
test: add automated tests and integrate them into Docker build
```

```text
feat: add OpenAPI documentation with Swagger UI
```

Changes are integrated into `main` through Pull Requests, allowing changes to be reviewed before merging.

After completing the initial functional version, the following tag was created:

```text
v0.1.0
```

This workflow keeps configuration changes, features, fixes, and tests separated while maintaining a clear and understandable Git history.

---

## Project Structure

```text
tu-llave/
├── docker-compose.yml
├── Dockerfile
├── LICENSE
├── Makefile
├── pom.xml
├── postman/
│   └── TuLlave-Recharge-API.postman_collection.json
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── tullave/
    │   │           └── recharge/
    │   │               ├── config/
    │   │               │   └── OpenApiConfig.java
    │   │               ├── controller/
    │   │               │   └── RechargeController.java
    │   │               ├── dto/
    │   │               │   ├── RechargeRequest.java
    │   │               │   └── RechargeResponse.java
    │   │               ├── entity/
    │   │               │   └── Recharge.java
    │   │               ├── enums/
    │   │               │   └── PaymentMethod.java
    │   │               ├── exception/
    │   │               │   ├── ErrorResponse.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── RechargeNotFoundException.java
    │   │               ├── repository/
    │   │               │   └── RechargeRepository.java
    │   │               ├── service/
    │   │               │   └── RechargeService.java
    │   │               └── TuLlaveRechargeApplication.java
    │   └── resources/
    │       └── application.properties
    │
    └── test/
        └── java/
            └── com/
                └── tullave/
                    └── recharge/
                        ├── controller/
                        │   └── RechargeControllerTest.java
                        └── service/
                            └── RechargeServiceTest.java
```

### Main directories

* `src/main/java` — Application source code.
* `src/main/resources` — Application configuration.
* `src/test/java` — Automated tests.
* `postman` — Postman collection with successful and error scenarios.

### Application layers

* `config` — Application configuration, including OpenAPI documentation.
* `controller` — REST API endpoints and HTTP request handling.
* `service` — Business logic and application operations.
* `repository` — Database access through Spring Data JPA.
* `entity` — JPA persistence entities.
* `dto` — API request and response models.
* `enums` — Application enumerations such as payment methods.
* `exception` — Custom exceptions, centralized exception handling, and error response models.

The `target/` directory is intentionally not included in the project structure because it is generated by Maven during compilation and testing and should not be committed to Git.


---

## Contact

Daniel Pinto Salazar

* GitHub: [danielpintosalazar](https://github.com/danielpintosalazar)
* LinkedIn: [Daniel Pinto Salazar](https://www.linkedin.com/in/daniel-pinto-salazar/)
* Email: [danielpintodev@gmail.com](mailto:danielpintodev@gmail.com)

