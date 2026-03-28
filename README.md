# Health Hub

A Spring Boot REST API backend for health and fitness tracking. Supports user management, body measurements, sleep logging, and workout planning/tracking.

## Tech Stack

- Java 21
- Spring Boot 3.5.0
- Spring Security (JWT-based, stateless)
- Spring Data JPA + PostgreSQL
- Flyway (database migrations)
- Bucket4j (rate limiting)

## Requirements

- Java 21
- Maven 3.x
- PostgreSQL

## Configuration

The application requires the following environment variables or properties:

| Property | Description |
|---|---|
| `spring.datasource.url` | PostgreSQL connection URL |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `jwt.secret` | JWT signing secret |
| `jwt.expiration` | JWT token expiration (ms) |
| `cors.allowed-origins` | Comma-separated list of allowed origins |
| `rate.limit.auth.capacity` | Max auth requests (default: 5) |
| `rate.limit.auth.refill-minutes` | Auth rate limit window in minutes (default: 1) |
| `rate.limit.api.capacity` | Max API requests (default: 100) |
| `rate.limit.api.refill-minutes` | API rate limit window in minutes (default: 1) |

## Running

```bash
mvn spring-boot:run
```

Server starts on `http://localhost:8080`.

## Build

```bash
mvn clean package
java -jar target/health-hub-0.0.1-SNAPSHOT.jar
```

## Authentication

All endpoints except `/api/auth/**` require a valid JWT bearer token:

```
Authorization: Bearer <token>
```

### Roles

- `SUPERADMIN` — full access to all resources
- `ADMIN` — (reserved for future use)
- `USER` — access to own resources only

## API Endpoints

### Users — `/api/users` _(SUPERADMIN only)_

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users` | Create user |
| `GET` | `/api/users?page=0&size=10` | List users (paginated) |
| `PUT` | `/api/users/{userId}` | Update user |
| `DELETE` | `/api/users/{userId}` | Delete user |

### User Profile — `/api/users/{userId}/profile`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/users/{userId}/profile` | Get profile |
| `PUT` | `/api/users/{userId}/profile` | Update profile |

### Body Measurements — `/api/users/{userId}/profile/measurements`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users/{userId}/profile/measurements` | Create measurement |
| `GET` | `/api/users/{userId}/profile/measurements?page=0&size=50` | List measurements (paginated) |
| `GET` | `/api/users/{userId}/profile/measurements/{measurementId}` | Get measurement |
| `PUT` | `/api/users/{userId}/profile/measurements/{measurementId}` | Update measurement |
| `DELETE` | `/api/users/{userId}/profile/measurements/{measurementId}` | Delete measurement |
| `GET` | `/api/users/{userId}/profile/measurements/weekly-averages?weeks=5` | Weekly averages |

### Sleep Logs — `/api/users/{userId}/sleep`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users/{userId}/sleep` | Create sleep log |
| `GET` | `/api/users/{userId}/sleep?page=0&size=50` | List sleep logs (paginated) |
| `GET` | `/api/users/{userId}/sleep/{sleepLogId}` | Get sleep log |
| `DELETE` | `/api/users/{userId}/sleep/{sleepLogId}` | Delete sleep log |
| `GET` | `/api/users/{userId}/sleep/weekly-averages?weeks=5` | Weekly averages |

### Workout Plans — `/api/users/{userId}/workout-plans`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users/{userId}/workout-plans` | Create plan |
| `GET` | `/api/users/{userId}/workout-plans` | List plans |
| `GET` | `/api/users/{userId}/workout-plans/{planId}` | Get plan |
| `PUT` | `/api/users/{userId}/workout-plans/{planId}` | Update plan |
| `DELETE` | `/api/users/{userId}/workout-plans/{planId}` | Delete plan |

### Workout Sessions — `/api/users/{userId}/workout-sessions`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/users/{userId}/workout-sessions` | Create session |
| `GET` | `/api/users/{userId}/workout-sessions?page=0&size=10` | List sessions (paginated, newest first) |
| `GET` | `/api/users/{userId}/workout-sessions/{sessionId}` | Get session |
| `PUT` | `/api/users/{userId}/workout-sessions/{sessionId}` | Update session |
| `DELETE` | `/api/users/{userId}/workout-sessions/{sessionId}` | Delete session |
