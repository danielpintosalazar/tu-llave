# TuLlave Recharges API Service

> Author: Daniel Pinto
> Date: October 4, 2026

## Project Overview

TuLlave Recharges API Service is a REST API developed with Java and Spring Boot to manage digital recharges for Bogotá's public transportation cards.

The service allows users to create, consult, and manage card recharge transactions according to the defined business rules, supported payment methods, and recharge amount constraints.

## Technology Stack

* Java 21
* Spring Boot 3
* Spring Data JPA
* PostgreSQL
* Docker
* Maven

## Project Purpose

This project was developed as part of a technical assessment to demonstrate:

* REST API development with Spring Boot
* Layered application architecture
* Data persistence with JPA and PostgreSQL
* Request validation and error handling
* Containerized application and database setup
* Clean and maintainable code practices

## Testing

The project includes automated tests using JUnit 5, Mockito, and Spring MVC Test.

### Test Coverage

The test suite covers the main recharge business flows and HTTP endpoints:

- Recharge creation with valid data.
- Request validation for card number and recharge amount.
- Recharge retrieval with pagination.
- Recharge retrieval filtered by card number.
- Pagination parameter validation.
- Recharge deletion.
- Handling of non-existing recharges.
- Invalid payment methods.
- Global exception handling.
- Service and repository interactions.

### Running Tests

Run the complete test suite with:

```bash
mvn clean test
```

## Postman Collection

A Postman collection is included in the `postman/` directory with successful and error scenarios for the recharge API.

The collection covers:

- Recharge creation.
- Request validation.
- Recharge retrieval with pagination.
- Recharge retrieval by card number.
- Pagination validation.
- Recharge deletion.
- Non-existing recharge handling.
- Invalid payment methods.

Import the collection into Postman and configure the `baseUrl` environment variable:

```text
http://localhost:8080
```