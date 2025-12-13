# Spring Boot OAuth Client

A Spring Boot 3.5 OAuth client application demonstrating OAuth 2.0 authorization code flow with JSP views.

## Features

- **Spring Boot 3.5.7** with Java 21
- **OAuth 2.0 Authorization Code Flow** - Complete OAuth client implementation
- **JSP Views** - Server-side rendered views with JSTL
- **REST Client** - Uses RestTemplate for OAuth token exchange
- **Employee Management** - Demo endpoint for fetching employee data

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Java | 21 |
| JSP | Jakarta Servlet JSP |
| JSTL | Jakarta JSTL |
| Tomcat Embed Jasper | 10.1.x |

## Project Structure

```
src/main/
├── java/com/rslaka/springboot/oauthclient/
│   ├── SpringBootOAuthClientApplication.java   # Main application
│   ├── controllers/
│   │   └── EmployeeController.java             # OAuth flow & employee endpoints
│   └── model/
│       └── Employee.java                       # Employee model
├── resources/
│   └── application.properties                  # Application configuration
└── webapp/WEB-INF/jsp/
    ├── getEmployees.jsp                        # Employee form view
    └── showEmployees.jsp                       # Employee list view
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- OAuth Authorization Server running (for full OAuth flow)

### Build

```bash
# Using build script
./buildMaven.sh

# Or directly with Maven
mvn clean package -DskipTests -Drevision=0.0.1-SNAPSHOT
```

### Run

```bash
# Using run script
./runMaven.sh

# Or directly
java -jar target/springboot-oauth-client.jar

# Or with Spring Boot Maven plugin
mvn spring-boot:run
```

The application will start on **http://localhost:8090**

## Configuration

Configure the OAuth server URL in `application.properties`:

```properties
server.port=8090
authServiceBaseUrl=http://localhost:8090

# JSP Configuration
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/getEmployees` | Display form to initiate OAuth flow |
| GET | `/showEmployees` | Display employee list (handles OAuth callback) |
| GET | `/showEmployees?code=<auth_code>` | OAuth callback with authorization code |

## OAuth Flow

1. **User visits** `/getEmployees` - Shows the employee request form
2. **User initiates OAuth** - Redirects to OAuth authorization server
3. **User authorizes** - OAuth server redirects back with authorization code
4. **Token exchange** - Application exchanges code for access token
5. **API call** - Application uses token to fetch employees
6. **Display results** - Shows employee list in `/showEmployees`

### OAuth Configuration

The application expects an OAuth server with:

- **Token endpoint**: `{authServiceBaseUrl}/oauth/token`
- **Resource endpoint**: `{authServiceBaseUrl}/user/getEmployeesList`
- **Client credentials**: `oauth:secret` (Base64 encoded)
- **Grant type**: `authorization_code`
- **Redirect URI**: `http://localhost:8090/showEmployees`

## Demo Mode

Without an OAuth server, the application provides demo data:

```bash
curl http://localhost:8090/showEmployees
```

Returns sample employees:
- Roh Lakra
- Roh Singh
- RS Lakra

## Migration Notes (Spring Boot 2.x → 3.x)

### Dependencies Changed

| Old (2.x) | New (3.x) |
|-----------|-----------|
| `javax.servlet:jstl` | `org.glassfish.web:jakarta.servlet.jsp.jstl` |
| Tomcat 9.x | Tomcat 10.1.x |

### Package Changes

| Old | New |
|-----|-----|
| `javax.servlet.*` | `jakarta.servlet.*` |

## References

- [Spring Boot OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)

## Author

**Rohtash Lakra**
