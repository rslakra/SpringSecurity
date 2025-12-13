# Java JWT Based Security

A Spring Boot 3.5 example demonstrating JWT (JSON Web Token) features using the [JJWT](https://github.com/jwtk/jjwt) library with CSRF protection and a modern interactive API Explorer UI.

## Features

- **Spring Boot 3.5.7** with Java 21
- **JJWT 0.12.6** - Fluent interface Java JWT library
- **JWT-based CSRF Protection** - Custom CSRF token repository using JWT
- **Dynamic JWT Generation** - Build JWTs with custom claims
- **JWT Parsing & Validation** - Parse and verify JWT signatures
- **Interactive API Explorer** - Modern dark-themed UI for testing APIs
- **Multiple Signature Algorithms** - HS256, HS384, HS512 support
- **Thymeleaf Templates** - Modular template fragments

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Security | 6.5.x |
| JJWT | 0.12.6 |
| Thymeleaf | 3.x |
| Java | 21 |

## Project Structure

```
src/main/
├── java/com/rslakra/springsecurity/jwtbasedsecurity/
│   ├── JavaJwtBasedSecurityApplication.java   # Main application entry
│   ├── config/
│   │   ├── CSRFConfig.java                    # CSRF configuration
│   │   ├── JWTCsrfTokenRepository.java        # JWT-based CSRF token repository
│   │   └── WebSecurityConfig.java             # Security configuration
│   ├── controller/
│   │   ├── BaseController.java                # Base controller with exception handling
│   │   ├── rest/
│   │   │   ├── DynamicJWTController.java      # Dynamic JWT generation endpoints
│   │   │   ├── SecretsController.java         # Secrets management
│   │   │   └── StaticJWTController.java       # Static JWT endpoints
│   │   └── web/
│   │       ├── FormController.java            # Form handling
│   │       └── HomeController.java            # Home page
│   ├── filter/
│   │   ├── FilterUtils.java                   # Filter utilities
│   │   └── JwtCsrfValidatorFilter.java        # JWT CSRF validation filter
│   ├── model/
│   │   └── JwtResponse.java                   # JWT response model
│   ├── service/
│   │   └── SecretsService.java                # Secrets management service
│   └── utils/
│       └── JWTUtils.java                      # JWT utility methods
└── resources/
    ├── static/
    │   ├── css/
    │   │   ├── styles.css                     # Base theme & utility styles
    │   │   └── api-styles.css                 # API Explorer component styles (reusable)
    │   └── js/
    │       └── actions.js                     # Interactive API functionality
    └── templates/
        ├── fragments/
        │   ├── head.html                      # Common head fragment
        │   └── footer.html                    # Common footer fragment
        ├── index.html                         # API Explorer home page
        ├── jwt-csrf-form.html                 # CSRF demo form
        ├── jwt-csrf-form-result.html          # CSRF success page
        └── expired-jwt.html                   # Token expired error page
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
# Using run script
./runMaven.sh

# Or directly
java -jar target/java-jwt-based-security.jar

# Or with Spring Boot Maven plugin
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

## API Explorer UI

The application includes a modern, interactive API Explorer UI:

![API Explorer](docs/api-explorer.png)

### Features

- **Dark Theme** - GitHub-inspired dark color scheme
- **Interactive Testing** - Try API endpoints directly from the browser
- **Syntax Highlighting** - JSON responses with color-coded keys, values, and types
- **Copy to Clipboard** - One-click copy for responses and cURL commands
- **HTTP Method Badges** - Color-coded GET, POST, PUT, DELETE badges
- **Loading States** - Visual feedback during API calls
- **Response Status** - Clear success/error status indicators

### CSS Architecture

The styles are modular and reusable:

| File | Purpose |
|------|---------|
| `styles.css` | Base theme, layout, utilities (can be used standalone) |
| `api-styles.css` | API card, code blocks, method badges (reusable component) |

To use the API Explorer styles in another project:
```html
<link rel="stylesheet" href="/css/styles.css"/>
<link rel="stylesheet" href="/css/api-styles.css"/>
<script src="/js/actions.js"></script>
```

## API Endpoints

### REST Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/static-builder` | Generate a static JWT with predefined claims |
| GET | `/parser?jwt=<token>` | Parse and decode a JWT token |
| GET | `/parser-enforce?jwt=<token>` | Parse JWT with required claims enforcement |
| POST | `/dynamic-builder-general` | Build JWT with any claims |
| POST | `/dynamic-builder-specific` | Build JWT with specific registered claims |
| POST | `/dynamic-builder-compress` | Build compressed JWT |
| GET | `/get-secrets` | Get current signing secrets |
| GET | `/refresh-secrets` | Generate new signing secrets |
| POST | `/set-secrets` | Set new signing secrets |

### Web Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` or `/index` | API Explorer home page |
| GET/POST | `/jwt-csrf-form` | CSRF form demo |
| GET | `/expired-jwt` | Expired JWT error page |

## Usage Examples

### Generate a Static JWT

```bash
curl http://localhost:8080/static-builder
```

Response:
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Parse a JWT Token

```bash
curl "http://localhost:8080/parser?jwt=eyJhbGciOiJIUzI1NiJ9..."
```

### Generate Dynamic JWT with Claims

```bash
curl -X POST http://localhost:8080/dynamic-builder-general \
  -H "Content-Type: application/json" \
  -d '{
    "sub": "rslakra",
    "name": "Rohtash Lakra",
    "admin": true
  }'
```

### Generate JWT with Specific Claims

```bash
curl -X POST http://localhost:8080/dynamic-builder-specific \
  -H "Content-Type: application/json" \
  -d '{
    "iss": "Rohtash Lakra",
    "sub": "rslakra",
    "exp": 4622470422,
    "iat": 1466796822
  }'
```

### Get Current Secrets

```bash
curl http://localhost:8080/get-secrets
```

### Refresh Secrets

```bash
curl http://localhost:8080/refresh-secrets
```

## Security Configuration

### JWT-based CSRF Protection

This project demonstrates using JWT tokens for CSRF protection:

1. **JWTCsrfTokenRepository** - Generates JWT tokens with expiration for CSRF
2. **JwtCsrfValidatorFilter** - Validates JWT CSRF tokens aren't expired
3. **Short-lived tokens** - CSRF tokens expire in 30 seconds

### Key Configuration

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterAfter(new JwtCsrfValidatorFilter(...), CsrfFilter.class)
            .csrf(csrf -> csrf
                .csrfTokenRepository(jwtCsrfTokenRepository)
                .ignoringRequestMatchers(ignoreCsrfAntMatchers)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
            );
        return http.build();
    }
}
```

## JJWT 0.12.x API

This project uses the modern JJWT 0.12.x API:

### Building a JWT

```java
String jwt = Jwts.builder()
    .issuer("Rohtash Lakra")
    .subject("rslakra")
    .claim("name", "Rohtash Lakra")
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + 3600000))
    .signWith(secretKey)
    .compact();
```

### Parsing a JWT

```java
Claims claims = Jwts.parser()
    .verifyWith(secretKey)
    .build()
    .parseSignedClaims(jwt)
    .getPayload();
```

### Generating Secure Keys

```java
// Generate a secure key for HS256 (256 bits minimum)
SecretKey key = Jwts.SIG.HS256.key().build();

// For HS384 (384 bits minimum)
SecretKey key384 = Jwts.SIG.HS384.key().build();

// For HS512 (512 bits minimum)
SecretKey key512 = Jwts.SIG.HS512.key().build();
```

### Key Requirements

> ⚠️ **Important**: JJWT 0.12.x enforces [RFC 7518](https://tools.ietf.org/html/rfc7518#section-3.2) key size requirements:
> - **HS256**: Key must be ≥ 256 bits (32 bytes)
> - **HS384**: Key must be ≥ 384 bits (48 bytes)  
> - **HS512**: Key must be ≥ 512 bits (64 bytes)

## Testing

Run all tests:

```bash
mvn test
```

Run with verbose output:

```bash
mvn test -Drevision=0.0.1-SNAPSHOT
```

### Test Coverage

| Test Class | Description |
|------------|-------------|
| `JWTUtilsTest` | JWT generation and parsing tests |
| `JavaJwtBasedSecurityApplicationTest` | Application context loading test |

## Migration from JJWT 0.9.x to 0.12.x

If upgrading from older JJWT versions, note these API changes:

| Old API (0.9.x) | New API (0.12.x) |
|-----------------|------------------|
| `Jwts.builder().setSubject(s)` | `Jwts.builder().subject(s)` |
| `Jwts.builder().setIssuer(s)` | `Jwts.builder().issuer(s)` |
| `Jwts.builder().setExpiration(d)` | `Jwts.builder().expiration(d)` |
| `Jwts.builder().setIssuedAt(d)` | `Jwts.builder().issuedAt(d)` |
| `Jwts.builder().setId(s)` | `Jwts.builder().id(s)` |
| `Jwts.builder().setClaims(m)` | `Jwts.builder().claims(m)` |
| `Jwts.builder().signWith(alg, key)` | `Jwts.builder().signWith(key)` |
| `Jwts.parser().setSigningKey(key)` | `Jwts.parser().verifyWith(key)` |
| `parser.parseClaimsJws(jwt)` | `parser.build().parseSignedClaims(jwt)` |
| `SignatureAlgorithm.HS256` | `Jwts.SIG.HS256` |
| `CompressionCodecs.DEFLATE` | `Jwts.ZIP.DEF` |

## Troubleshooting

### WeakKeyException

```
The specified key byte array is X bits which is not secure enough
```

**Solution**: Use `Jwts.SIG.HS256.key().build()` to generate secure keys, or ensure your secret is at least 32 characters for HS256.

### SignatureException

```
JWT signature does not match
```

**Solution**: Ensure you're using the same secret key for signing and verification.

### ExpiredJwtException

```
JWT expired at [time]
```

**Solution**: Generate a new token. CSRF tokens in this demo expire after 30 seconds intentionally.

## References

- [JJWT GitHub](https://github.com/jwtk/jjwt)
- [JWT.io](https://jwt.io/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)
- [RFC 7518 - JWA Algorithms](https://tools.ietf.org/html/rfc7518)

## Author

**Rohtash Lakra**
