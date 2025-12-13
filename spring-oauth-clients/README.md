# Spring OAuth Clients

A collection of Spring Boot OAuth 2.0 applications demonstrating authorization server and client implementations.

## Projects

| Project | Description | Port | Spring Boot |
|---------|-------------|------|-------------|
| [springboot-oauth2-service](./springboot-oauth2-service) | OAuth 2.0 Authorization Server with Spring Authorization Server | 8080 | 3.5.7 |
| [springboot-oauth-client](./springboot-oauth-client) | OAuth 2.0 Client with JSP views | 8090 | 3.5.7 |

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           OAuth 2.0 Flow                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│   ┌──────────┐     ┌────────────────────┐     ┌────────────────────┐    │
│   │  User    │────▶│  springboot-       │────▶│  springboot-       │    │
│   │ Browser  │     │  oauth-client      │     │  oauth2-service    │    │
│   │          │     │  (Port 8090)       │     │  (Port 8080)       │    │
│   └──────────┘     └────────────────────┘     └────────────────────┘    │
│        │                    │                          │                │
│        │ 1. /getEmployees   │                          │                │
│        │───────────────────▶│                          │                │
│        │                    │ 2. Redirect to           │                │
│        │                    │    /oauth2/authorize     │                │
│        │◀───────────────────│─────────────────────────▶│                │
│        │                    │                          │                │
│        │ 3. Login & Consent │                          │                │
│        │◀──────────────────────────────────────────────│                │
│        │                    │                          │                │
│        │ 4. Callback with   │                          │                │
│        │    auth code       │ 5. POST /oauth2/token    │                │
│        │───────────────────▶│─────────────────────────▶│                │
│        │                    │ 6. Access Token (JWT)    │                │
│        │                    │◀─────────────────────────│                │
│        │ 7. Show Employees  │                          │                │
│        │◀───────────────────│                          │                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Authorization Server | 1.5.x |
| Java | 21 |
| Maven | 3.8+ |

## Quick Start

### 1. Start the Authorization Server

```bash
cd springboot-oauth2-service
./buildMaven.sh
java -jar target/springboot-oauth2-service.jar
```

Server runs at: **http://localhost:8080**

### 2. Start the OAuth Client

```bash
cd springboot-oauth-client
./buildMaven.sh
java -jar target/springboot-oauth-client.jar
```

Client runs at: **http://localhost:8090**

### 3. Test the Flow

1. Open http://localhost:8090
2. Click "Try OAuth Flow"
3. Login with default credentials (see below)
4. View the protected employee data

## Default Users

The authorization server creates these users on first startup:

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ROLE_ADMIN |
| `user` | `user123` | ROLE_USER |

## Projects Overview

### springboot-oauth2-service (Authorization Server)

A complete OAuth 2.0 Authorization Server using Spring Authorization Server:

- **Spring Authorization Server** - Modern OAuth2/OIDC implementation
- **JWT Tokens** - RSA-signed access tokens
- **Multiple Clients** - Configurable OAuth2 clients
- **OIDC Support** - OpenID Connect discovery endpoint
- **User Management** - REST API for user CRUD operations

**OAuth Endpoints:**
| Endpoint | Description |
|----------|-------------|
| `/.well-known/openid-configuration` | OIDC discovery |
| `/oauth2/authorize` | Authorization endpoint |
| `/oauth2/token` | Token endpoint |
| `/oauth2/jwks` | JWK Set (public keys) |

**Configuration:**
```properties
oauth.client.id=oauth
oauth.client.secret={bcrypt}$2a$10$...
oauth.client.redirect-uris=http://localhost:8090/showEmployees
oauth.client.scopes=openid,read,write
```

### springboot-oauth-client (Resource Client)

A complete OAuth 2.0 client implementation:

- **Authorization Code Flow** - Full OAuth 2.0 authorization code grant
- **JSP Views** - Server-side rendered views with Jakarta JSTL
- **Token Exchange** - Exchanges authorization code for access token
- **Demo Mode** - Works without OAuth server for testing

**Endpoints:**
| Endpoint | Description |
|----------|-------------|
| `/` | API Explorer home page |
| `/getEmployees` | Initiate OAuth flow |
| `/showEmployees` | Display employee list (OAuth callback) |

## Testing Without OAuth Server

The OAuth client supports **Demo Mode** - visit `/showEmployees` directly to see sample data without requiring the authorization server.

```bash
curl http://localhost:8090/showEmployees
```

## OAuth Client Credentials

| Client ID | Secret | Redirect URI |
|-----------|--------|--------------|
| `oauth` | `secret` | `http://localhost:8090/showEmployees` |
| `fooClientId` | `secret` | `http://localhost:8080/callback` |

## Logging & Debugging

Both projects use Logback for logging. To debug OAuth issues, update `logback.xml`:

```xml
<!-- Change INFO to DEBUG for OAuth troubleshooting -->
<logger name="org.springframework.security" level="DEBUG"/>
<logger name="org.springframework.security.oauth2" level="DEBUG"/>
```

## Migration from Spring Boot 2.x to 3.x

All projects have been migrated to Spring Boot 3.5.7:

| Component | Old (2.x) | New (3.x) |
|-----------|-----------|-----------|
| OAuth Server | `spring-security-oauth2` (deprecated) | `spring-security-oauth2-authorization-server` |
| Security Config | `WebSecurityConfigurerAdapter` | `SecurityFilterChain` bean |
| Servlet API | `javax.servlet.*` | `jakarta.servlet.*` |
| JSTL | `javax.servlet:jstl` | `org.glassfish.web:jakarta.servlet.jsp.jstl` |
| Java | 8/11 | 21 |

## References

- [Spring Authorization Server](https://docs.spring.io/spring-authorization-server/reference/)
- [Spring Security OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)
- [OpenID Connect Core](https://openid.net/specs/openid-connect-core-1_0.html)

## Author

**Rohtash Lakra**
