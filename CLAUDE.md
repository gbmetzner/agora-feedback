# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Quarkus-based REST API** for managing feedback, **deployed as AWS Lambda functions**. The project uses:
- **Framework**: Quarkus 3.29.3 (Java 21) with AWS Lambda HTTP extension
- **Deployment**: AWS Lambda (serverless)
- **Database**: PostgreSQL with Flyway migrations
- **Build Tool**: Gradle
- **Testing**: JUnit 5 with REST Assured

The application follows a **domain-driven architecture** with layers for resources (REST endpoints), entities (JPA models), and repositories (data access).

## Architecture

### Core Layers

1. **Resources** (`src/main/java/com/agora/domain/feedback/resource/`)
   - REST endpoints using Jakarta REST
   - Entry points for API consumers
   - Example: `FeedbackResource` handles the `/hello` endpoint

2. **Entities** (`src/main/java/com/agora/domain/feedback/model/entity/`)
   - JPA/Hibernate entities extending `PanacheEntityBase`
   - Main domain models: `Feedback`, `FeedbackCategory`, `FeedbackStatus`
   - Use Quarkus Panache for simplified ORM

3. **Repositories** (`src/main/java/com/agora/domain/feedback/model/repository/`)
   - Data access layer implementing `PanacheRepository<T>`
   - Provides query and persistence methods
   - Example: `FeedbackRepository` provides CRUD operations for `Feedback`

### Database

- Migrations are located in `src/main/resources/db/migration/`
- Uses Flyway for schema versioning (auto-runs on startup via `migrate-at-start: true`)
- PostgreSQL configuration in `application.yaml`

## Common Development Commands

### Build and Run

```bash
# Development mode (live coding, DevUI at http://localhost:8080/q/dev/)
./gradlew quarkusDev

# Build JAR for production
./gradlew build

# Build uber-JAR (all dependencies included)
./gradlew build -Dquarkus.package.jar.type=uber-jar

# Build native executable (requires GraalVM)
./gradlew build -Dquarkus.native.enabled=true
```

### Testing

```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests FeedbackResourceTest

# Run a single test method
./gradlew test --tests FeedbackResourceTest.testHelloEndpoint
```

### Database

- Dev mode uses auto-provisioned PostgreSQL container (DevServices)
- Configuration in `src/main/resources/application.yaml`
- Flyway automatically runs migrations on startup

## AWS Lambda Deployment

The `quarkus-amazon-lambda-http` extension enables this Quarkus application to run in AWS Lambda. Key considerations:

- **Cold Starts**: Lambda cold starts impact performance; Quarkus is optimized for fast startup with native builds
- **Container Build**: Use `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true` to build native executables in Docker if GraalVM is unavailable
- **Database Connections**: PostgreSQL is accessed from Lambda; ensure RDS is in the same VPC or publicly accessible
- **API Gateway**: The Lambda function integrates with API Gateway; REST endpoints map directly to API Gateway routes
- **Stateless Design**: Lambda functions are ephemeral; don't rely on local state

## Key Notes

- **Panache Integration**: Entities extend `PanacheEntityBase` and repositories implement `PanacheRepository` - this eliminates boilerplate for basic CRUD operations
- **Dependency Injection**: Uses Quarkus CDI/Arc; inject dependencies via constructor or `@Inject`
- **Transactional Resources**: REST endpoints need `@Transactional` annotation when accessing the database
- **API Documentation**: SmallRye OpenAPI is configured; Swagger UI available at `/q/swagger-ui` in dev mode
- add to memory: use TDD, DDD, and always use best practices (syntax, naming convention, java best practices, sql best practices, and so on)