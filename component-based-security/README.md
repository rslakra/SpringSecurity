# Component-Based Security

A Spring Boot 3.5 JWT-based authentication and authorization example using component-based security configuration.

## Features

- **Spring Boot 3.5.7** with Java 21
- **JWT Authentication** using JJWT 0.12.6
- **Component-based Security Configuration** (no deprecated `WebSecurityConfigurerAdapter`)
- **H2 Database** for development/testing
- **MySQL Support** for production
- **Stateless Session Management**
- **BCrypt Password Encoding**

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Security | 6.5.x |
| JJWT | 0.12.6 |
| H2 Database | 2.3.x |
| Java | 21 |
| Hibernate | 6.6.x |

## Project Structure

```
src/main/java/com/rslakra/componentbasedsecurity/
├── ComponentBasedSecurityApplication.java    # Main application entry
├── config/
│   ├── JwtAuthenticationEntryPoint.java      # Handles unauthorized access
│   ├── JwtRequestFilter.java                 # JWT token validation filter
│   ├── JwtUtils.java                         # JWT token generation/parsing
│   ├── Keys.java                             # Constant keys
│   ├── PasswordEncoderConfig.java            # BCrypt password encoder bean
│   └── WebSecurityConfig.java                # Security filter chain configuration
├── controller/
│   ├── HomeController.java                   # Protected endpoints
│   └── JwtAuthenticationController.java      # Auth endpoints (register/login)
├── payload/
│   ├── dto/
│   │   └── UserDTO.java                      # User data transfer object
│   ├── Request.java                          # Auth request payload
│   └── Response.java                         # Generic response payload
├── persistence/
│   ├── dao/
│   │   └── UserRepository.java               # User JPA repository
│   └── model/
│       └── User.java                         # User entity
└── service/
    ├── JwtUserService.java                   # JWT user service interface
    └── UserDetailsServiceImpl.java           # UserDetailsService implementation
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Build

```bash
# Using build script
./buildMaven.sh

# Or directly with Maven
mvn clean package -DskipTests
```

### Run

```bash
java -jar target/component-based-security.jar
```

The application will start on **http://localhost:8080**

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register a new user |
| POST | `/authenticate` | Login and get JWT token |
| GET | `/h2/**` | H2 Console (dev only) |

### Protected Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Welcome message |
| GET | `/home` | Home page |

## Usage Examples

### 1. Register a New User

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "password": "password123"
  }'
```

### 2. Authenticate (Get JWT Token)

```bash
curl -X POST http://localhost:8080/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "testuser",
    "password": "password123"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Access Protected Endpoint

```bash
curl -X GET http://localhost:8080/ \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

Response:
```json
{
  "message": "Welcome, testuser."
}
```

## Configuration

### Application Properties

```properties
# JWT Configuration
app.jwtSecret = your-secret-key-at-least-32-characters
app.jwtExpirationInMillis = 86400000

# H2 Database
spring.datasource.url = jdbc:h2:file:~/Downloads/H2DB/ComponentBasedSecurity
spring.h2.console.enabled = true
spring.h2.console.path = /h2

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto = update
spring.jpa.show-sql = true
```

### H2 Console Access

Navigate to **http://localhost:8080/h2** to access the H2 database console.

- **JDBC URL**: `jdbc:h2:file:~/Downloads/H2DB/ComponentBasedSecurity`
- **Username**: `sa`
- **Password**: *(empty)*

## Security Configuration

This project uses **component-based security configuration** introduced in Spring Security 5.7+:

- `SecurityFilterChain` bean instead of `WebSecurityConfigurerAdapter`
- `@EnableMethodSecurity` instead of `@EnableGlobalMethodSecurity`
- Lambda DSL for HttpSecurity configuration
- Jakarta EE 10 (`jakarta.*` packages)

## License

This project is for educational purposes.

## Author

**Rohtash Lakra**

