# Spring Boot OAuth2 Authorization Server

A Spring Boot 3.5 OAuth2 Authorization Server using Spring Authorization Server.

## Features

- **Spring Boot 3.5.7** with Java 21
- **Spring Authorization Server** - Modern OAuth2/OIDC implementation
- **JWT Tokens** - RSA-signed access tokens
- **Multiple Clients** - Configurable OAuth2 clients
- **H2 Database** - User management with JPA
- **Externalized Configuration** - OAuth clients configured via properties

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Authorization Server | 1.5.x |
| Java | 21 |
| H2 Database | Runtime |

## Project Structure

```
src/main/java/com/rslaka/springsecurity/oauth2/
├── SpringSecurityOauth2Application.java    # Main application
├── configuration/
│   ├── AuthorizationServerConfig.java      # OAuth2 authorization server
│   ├── SecurityConfig.java                 # Spring Security config
│   ├── PasswordEncoderConfig.java          # Password encoder bean
│   ├── OAuthClientProperties.java          # OAuth client properties binding
│   └── DataInitializer.java                # Default user initialization
├── controller/
│   └── UserController.java                 # User management REST API
├── model/
│   └── UserInfo.java                       # User entity
├── repository/
│   └── UserInfoRepository.java             # User JPA repository
└── service/
    ├── UserDetailsServiceImpl.java         # Spring Security UserDetailsService
    └── UserInfoService.java                # User business logic
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+

### Build

```bash
./buildMaven.sh

# Or directly with Maven
mvn clean package -DskipTests -Drevision=0.0.1-SNAPSHOT
```

### Run

```bash
./runMaven.sh

# Or directly
java -jar target/springboot-oauth2-service.jar
```

The server will start on **http://localhost:8080**

## Default Users

On first startup, the following users are automatically created:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ROLE_ADMIN |
| `user` | `user123` | ROLE_USER |

> These users are created only if the database is empty. You can create additional users via the REST API.

## OAuth2 Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /.well-known/openid-configuration` | OIDC discovery document |
| `POST /oauth2/authorize` | Authorization endpoint |
| `POST /oauth2/token` | Token endpoint |
| `POST /oauth2/introspect` | Token introspection |
| `POST /oauth2/revoke` | Token revocation |
| `GET /oauth2/jwks` | JWK Set endpoint |
| `GET /userinfo` | OIDC UserInfo endpoint |

## Configuration

### OAuth Client Configuration

OAuth clients are configured in `application.properties`:

```properties
# OAuth Client Configuration
oauth.client.id=oauth
oauth.client.secret={bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubDNaLTqwuIlLxqgL2tRhD6pIS
oauth.client.redirect-uris=http://localhost:8090/showEmployees,http://localhost:8090/callback
oauth.client.scopes=openid,read,write

# Token Settings
oauth.token.access-token-ttl-minutes=3
oauth.token.refresh-token-ttl-minutes=10
```

---

## 🔐 Generating Client Secrets

The `oauth.client.secret` uses BCrypt encoding with the `{bcrypt}` prefix.

### Method 1: Using Spring's BCryptPasswordEncoder (Recommended)

Create a simple Java class or use JShell:

```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateSecret {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawSecret = "my-secret-password";
        String encoded = encoder.encode(rawSecret);
        System.out.println("{bcrypt}" + encoded);
    }
}
```

### Method 2: Using Spring Boot CLI

```bash
# If you have Spring Boot CLI installed
spring encodepassword my-secret-password
```

### Method 3: Online BCrypt Generator

Use https://bcrypt-generator.com/ with cost factor 10, then prefix with `{bcrypt}`.

### Method 4: Using cURL with a running app

Add this temporary endpoint to generate secrets:

```java
@GetMapping("/generate-secret")
public String generateSecret(@RequestParam String secret) {
    return "{bcrypt}" + new BCryptPasswordEncoder().encode(secret);
}
```

### Example

| Raw Secret | Encoded Secret |
|------------|----------------|
| `secret` | `{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubDNaLTqwuIlLxqgL2tRhD6pIS` |
| `my-app-secret` | `{bcrypt}$2a$10$...` (generate your own) |

> ⚠️ **Note**: Each BCrypt encoding produces a different hash even for the same input. This is by design (uses random salt). Any valid BCrypt hash of your secret will work.

---

## 💾 Database-Backed Client Repository (Alternative)

Instead of storing clients in `application.properties`, you can use a **JDBC-backed client repository** to store OAuth clients in the database.

### Step 1: Add JDBC Repository Configuration

Replace `InMemoryRegisteredClientRepository` with `JdbcRegisteredClientRepository`:

```java
@Configuration
public class AuthorizationServerConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }
}
```

### Step 2: Create Database Schema

Spring Authorization Server provides the schema. Add to `schema.sql`:

```sql
CREATE TABLE oauth2_registered_client (
    id varchar(100) NOT NULL,
    client_id varchar(100) NOT NULL,
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret varchar(200) DEFAULT NULL,
    client_secret_expires_at timestamp DEFAULT NULL,
    client_name varchar(200) NOT NULL,
    client_authentication_methods varchar(1000) NOT NULL,
    authorization_grant_types varchar(1000) NOT NULL,
    redirect_uris varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris varchar(1000) DEFAULT NULL,
    scopes varchar(1000) NOT NULL,
    client_settings varchar(2000) NOT NULL,
    token_settings varchar(2000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL,
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    authorized_scopes varchar(1000) DEFAULT NULL,
    attributes blob DEFAULT NULL,
    state varchar(500) DEFAULT NULL,
    authorization_code_value blob DEFAULT NULL,
    authorization_code_issued_at timestamp DEFAULT NULL,
    authorization_code_expires_at timestamp DEFAULT NULL,
    authorization_code_metadata blob DEFAULT NULL,
    access_token_value blob DEFAULT NULL,
    access_token_issued_at timestamp DEFAULT NULL,
    access_token_expires_at timestamp DEFAULT NULL,
    access_token_metadata blob DEFAULT NULL,
    access_token_type varchar(100) DEFAULT NULL,
    access_token_scopes varchar(1000) DEFAULT NULL,
    oidc_id_token_value blob DEFAULT NULL,
    oidc_id_token_issued_at timestamp DEFAULT NULL,
    oidc_id_token_expires_at timestamp DEFAULT NULL,
    oidc_id_token_metadata blob DEFAULT NULL,
    refresh_token_value blob DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT NULL,
    refresh_token_expires_at timestamp DEFAULT NULL,
    refresh_token_metadata blob DEFAULT NULL,
    user_code_value blob DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata blob DEFAULT NULL,
    device_code_value blob DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata blob DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    authorities varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);
```

### Step 3: Create Admin REST API for Client Management

```java
@RestController
@RequestMapping("/admin/clients")
public class OAuthClientController {
    
    private final RegisteredClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> createClient(@RequestBody ClientRequest request) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(request.getClientId())
            .clientSecret("{bcrypt}" + passwordEncoder.encode(request.getClientSecret()))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .redirectUri(request.getRedirectUri())
            .scope("openid")
            .scope("read")
            .build();
        
        clientRepository.save(client);
        return ResponseEntity.ok("Client created: " + request.getClientId());
    }
}
```

---

## Testing OAuth Flow

### 1. Get Authorization Code

Open in browser:
```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=oauth&redirect_uri=http://localhost:8090/showEmployees&scope=openid%20read
```

### 2. Exchange Code for Token

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u oauth:secret \
  -d "grant_type=authorization_code" \
  -d "code=YOUR_AUTH_CODE" \
  -d "redirect_uri=http://localhost:8090/showEmployees"
```

### 3. Client Credentials Flow

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u oauth:secret \
  -d "grant_type=client_credentials" \
  -d "scope=read"
```

## H2 Console

Access the H2 database console at: **http://localhost:8080/h2**

| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:file:~/Downloads/H2DB/SpringBootOAuth2Service` |
| Username | `sa` |
| Password | *(empty)* |

You can view user data in the `USERS` table.

## Migration Notes (Spring Boot 2.x → 3.x)

| Old (Spring Security OAuth 2.x) | New (Spring Authorization Server) |
|---------------------------------|-----------------------------------|
| `@EnableAuthorizationServer` | `OAuth2AuthorizationServerConfigurer` |
| `AuthorizationServerConfigurerAdapter` | `SecurityFilterChain` bean |
| `ClientDetailsServiceConfigurer` | `RegisteredClientRepository` |
| `JwtAccessTokenConverter` | `JWKSource<SecurityContext>` |
| `javax.*` packages | `jakarta.*` packages |

## References

- [Spring Authorization Server Docs](https://docs.spring.io/spring-authorization-server/reference/)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)

## Author

**Rohtash Lakra**
