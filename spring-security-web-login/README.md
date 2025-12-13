# Spring Security Web Login

A Spring Boot application demonstrating custom authentication mechanisms with Spring Security, including custom authentication filters and providers.

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Spring Security | 6.x |
| Thymeleaf | (managed by Spring Boot) |

## Features

- Custom `PasswordAuthenticationFilter` extending `UsernamePasswordAuthenticationFilter`
- Custom `UserDetailsAuthenticationProvider` for authentication
- Custom `PasswordAuthenticationToken` supporting domain-based authentication
- Form-based login with custom error handling
- BCrypt password encoding

## Build the Project

```bash
mvn clean install
```

## Run the Project

### Using Maven Cargo Plugin

```bash
mvn cargo:run
```

### Using Spring Boot

```bash
java -jar target/spring-security-web-login.war
```

The application runs at: **http://localhost:8082/spring-security-web-login**

## Access

- **Application URL**: http://localhost:8081 (or http://localhost:8082/spring-security-web-login when using Cargo)
- **Home page**: http://localhost:8081/index
- **Login page**: http://localhost:8081/login
- **User dashboard**: http://localhost:8081/user/index (requires authentication)

### Login Credentials

The application uses domain-based authentication. Example credentials:
- Username: `rlakra`
- Domain: (varies based on your user configuration)
- Password: `test`

**Note**: The login form requires username, domain, and password fields.

## Security Configuration

The project uses Spring Security 6.x with `SecurityFilterChain`:

- **Custom Authentication Filter**: `PasswordAuthenticationFilter` handles domain-based authentication
- **Custom Authentication Provider**: `UserDetailsAuthenticationProvider` processes authentication tokens
- **Password Encoding**: BCrypt password encoder for secure password storage
- **Form Login**: Custom login page with error handling

## View Technology

The application uses **Thymeleaf** for server-side rendering:
- View templates are located in `src/main/resources/html/`
- Configured via `spring.thymeleaf.prefix = classpath:/html/` in `application.properties`
- Templates use Thymeleaf attributes (`th:`, `sec:`) for dynamic content
- **Note**: JSP files in `src/main/webapp/WEB-INF/view/` are legacy/unused files from previous versions

## Project Structure

```
spring-security-web-login/
├── src/main/java/com/rslakra/springsecurity/
│   ├── config/
│   │   ├── SecurityConfig.java              # Security filter chain configuration
│   │   └── PasswordEncoderConfiguration.java # Password encoder configuration
│   ├── filter/
│   │   └── PasswordAuthenticationFilter.java # Custom authentication filter
│   ├── service/
│   │   ├── UserDetailService.java           # User details service interface
│   │   ├── UserDetailServiceImpl.java        # User details service implementation
│   │   ├── UserDetailsAuthenticationProvider.java # Custom authentication provider
│   │   └── PasswordAuthenticationToken.java  # Custom authentication token
│   └── controller/
│       └── WebController.java                # Web controllers
├── src/main/resources/
│   ├── application.properties                # Application configuration
│   ├── html/                                 # Thymeleaf templates (ACTIVE)
│   │   ├── index.html
│   │   ├── login.html
│   │   └── user/
│   │       └── index.html
│   └── logback.xml                           # Logging configuration
└── src/main/webapp/WEB-INF/view/            # Legacy JSP files (UNUSED)
    ├── login.jsp
    ├── homepage.jsp
    ├── anonymous.jsp
    ├── accessDenied.jsp
    └── admin/
        └── adminpage.jsp
```

## Key Changes from Spring Boot 2.x

- **Spring Boot**: Upgraded from 2.6.4 to 3.5.7
- **Jakarta EE**: Migrated from `javax.servlet` to `jakarta.servlet`
- **Spring Security**: Updated to 6.x with lambda DSL
  - Replaced deprecated `authorizeRequests()` with `authorizeHttpRequests()`
  - Replaced `antMatchers()` with `requestMatchers()`
  - Removed `AbstractHttpConfigurer` pattern in favor of `@Configuration` class
- **Thymeleaf**: Updated Spring Security extras from version 5 to 6
- **View Technology**: Using Thymeleaf HTML templates (JSP files are legacy/unused)
- **Testing**: Updated tests to use `@SpringBootTest` instead of XML-based configuration

## Legacy Files

The following files are from previous versions and are no longer used:
- `src/main/webapp/WEB-INF/view/*.jsp` - Legacy JSP view files
- `src/main/resources/redirection-web-security-config.xml` - Old XML-based security configuration
- `src/main/webapp/WEB-INF/mvc-servlet.xml` - Legacy servlet configuration

These files can be safely removed if desired, but are kept for reference.


## Author

**Rohtash Lakra**
