# Spring Security Examples

A collection of Spring Security demonstration projects showcasing various authentication and authorization mechanisms, security configurations, and best practices.

## Overview

This repository contains multiple Spring Boot projects demonstrating different aspects of Spring Security, from basic authentication to OAuth2, JWT tokens, and custom authentication providers. All projects have been upgraded to use modern Spring Boot 3.5.7 and Spring Security 6.x with Jakarta EE standards.

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Spring Security | 6.x |
| Jakarta EE | 9+ (migrated from javax) |

## Projects

### 1. [spring-security-filter-chain](./spring-security-filter-chain/)

**Modern SecurityFilterChain Configuration**

Demonstrates the modern `SecurityFilterChain` approach for configuring Spring Security, replacing the deprecated `WebSecurityConfigurerAdapter`.

**Features:**
- Spring Security 6.x lambda DSL
- Role-based access control (USER, ADMIN)
- Form-based authentication
- Custom authentication entry point (401 for API, redirect for browsers)
- Thymeleaf templates with modern UI

**Tech:** Spring Boot 3.5.7, Thymeleaf, H2 Database

**Default Users:**
- `user` / `userPass` (USER role)
- `admin` / `adminPass` (ADMIN, USER roles)

---

### 2. [spring-security-web-login](./spring-security-web-login/)

**Custom Authentication Filter and Provider**

Demonstrates custom authentication mechanisms with domain-based authentication support.

**Features:**
- Custom `PasswordAuthenticationFilter` extending `UsernamePasswordAuthenticationFilter`
- Custom `UserDetailsAuthenticationProvider` for authentication
- Custom `PasswordAuthenticationToken` supporting domain-based authentication
- BCrypt password encoding
- Thymeleaf templates

**Tech:** Spring Boot 3.5.7, Thymeleaf, WAR packaging

**Note:** Uses Thymeleaf HTML templates (`src/main/resources/html/`). JSP files in `src/main/webapp/WEB-INF/view/` are legacy/unused.

---

### 3. [spring-oauth-clients](./spring-oauth-clients/)

**OAuth2 Authorization Server and Client**

Complete OAuth2 implementation with Spring Authorization Server and OAuth2 client.

**Sub-projects:**
- **springboot-oauth2-service**: OAuth2 Authorization Server using Spring Authorization Server
- **springboot-oauth-client**: OAuth2 client application

**Features:**
- Spring Authorization Server (replacing deprecated Spring Security OAuth2)
- JWT token customization
- Externalized OAuth client configuration
- Default user initialization
- JSP-based UI with fragments

**Tech:** Spring Boot 3.5.7, Spring Authorization Server, JSP/JSTL

---

### 4. [java-jwt-based-security](./java-jwt-based-security/)

**JWT-based Security with CSRF Protection**

Demonstrates JWT token generation, validation, and CSRF protection using JJWT library.

**Features:**
- JWT token generation and validation
- CSRF protection using JWT
- API explorer interface
- Thymeleaf templates with modern UI

**Tech:** Spring Boot, JJWT, Thymeleaf

---

### 5. [jwt-authentications](./jwt-authentications/)

**Multiple JWT Authentication Implementations**

Collection of JWT authentication examples with different approaches and configurations.

**Sub-projects:**
- `jwt-authentication1` through `jwt-authentication7`
- Various JWT implementation patterns
- All upgraded to Spring Boot 3.5.7

**Tech:** Spring Boot 3.5.7, Spring Security 6.x, JJWT, JPA

---

### 6. [component-based-security](./component-based-security/)

**Component-based Security Configuration**

Demonstrates component-based approach to Spring Security configuration.

---

### 7. [enable-method-security](./enable-method-security/)

**Method-level Security**

Demonstrates `@EnableMethodSecurity` and method-level authorization annotations.

---

## Common Migration Notes

All projects have been upgraded from Spring Boot 2.x to 3.5.7 with the following changes:

### Key Migrations

1. **Jakarta EE Migration**
   - `javax.servlet` → `jakarta.servlet`
   - `javax.persistence` → `jakarta.persistence`
   - `javax.transaction` → `jakarta.transaction`

2. **Spring Security 6.x**
   - `WebSecurityConfigurerAdapter` → `SecurityFilterChain` beans
   - `authorizeRequests()` → `authorizeHttpRequests()`
   - `antMatchers()` → `requestMatchers()`
   - Lambda DSL for configuration

3. **Dependencies**
   - Thymeleaf Spring Security extras: 5 → 6
   - JSP/JSTL: `javax.servlet.jsp.jstl` → `jakarta.servlet.jsp.jstl`
   - Removed explicit Spring Security dependencies (managed by Spring Boot)

4. **Testing**
   - Updated to use `@SpringBootTest` instead of XML-based configuration
   - Modern JUnit 5 testing

## Quick Start

### Prerequisites

- Java 21
- Maven 3.6+

### Build a Project

```bash
cd <project-directory>
./buildMaven.sh
# or
mvn clean package
```

### Run a Project

```bash
cd <project-directory>
./runMaven.sh
# or
java -jar target/<project-name>.jar
```

## Project Status

| Project | Spring Boot | Status | Notes |
|---------|------------|--------|-------|
| spring-security-filter-chain | 3.5.7 | ✅ Upgraded | Modern UI, all tests passing |
| spring-security-web-login | 3.5.7 | ✅ Upgraded | Custom auth, tests passing |
| spring-oauth-clients | 3.5.7 | ✅ Upgraded | Spring Authorization Server |
| java-jwt-based-security | 3.5.7 | ✅ Upgraded | JWT with CSRF protection |
| jwt-authentications/* | 3.5.7 | ✅ Upgraded | Multiple JWT implementations |
| component-based-security | 3.5.7 | ✅ Upgraded | Component-based security |
| enable-method-security | 3.5.7 | ✅ Upgraded | Method-level security |

## Documentation

Each project contains its own `README.md` with:
- Project-specific features
- Build and run instructions
- Configuration details
- API endpoints
- Default credentials

d## Duplicate Projects Analysis

Some projects in this repository are duplicates or very similar. See [DUPLICATE_ANALYSIS.md](./DUPLICATE_ANALYSIS.md) for a detailed analysis of:
- Which projects are duplicates
- What makes them similar or different
- Recommendations on which projects to keep or remove
- Comparison matrix of all jwt-authentication projects

## Contributing

When upgrading projects:
1. Update Spring Boot to 3.5.7
2. Migrate `javax` to `jakarta`
3. Update Spring Security to 6.x lambda DSL
4. Update dependencies
5. Fix compilation errors
6. Update tests
7. Update README.md

## License

This repository contains educational examples and demonstrations of Spring Security features.

## References

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Authorization Server](https://docs.spring.io/spring-authorization-server/reference/)
- [Jakarta EE Migration Guide](https://jakarta.ee/specifications/servlet/6.0/)



## Author

**Rohtash Lakra**
