# Enable Method Security

A Spring Boot 3.5 example demonstrating `@EnableMethodSecurity` with custom authorization using `AuthorizationManager`.

## Features

- **Spring Boot 3.5.7** with Java 21
- **@EnableMethodSecurity** annotation for method-level security
- **Custom AuthorizationManager** implementation
- **Policy-based Authorization** using custom annotations
- **HTTP Basic Authentication**
- **Stateless Session Management**
- **BCrypt Password Encoding**
- **Custom UserDetails** implementation with policy-based access control

## Tech Stack

| Technology | Version |
|------------|---------|
| Spring Boot | 3.5.7 |
| Spring Security | 6.5.x |
| Java | 21 |

## Project Structure

```
src/main/java/com/rslakra/enablemethodsecurity/
├── EnableMethodSecurityApplication.java      # Main application entry
├── authentication/
│   └── UserDetailsServiceImpl.java           # Custom UserDetailsService
├── authorization/
│   └── AuthorizationManagerImpl.java         # Custom AuthorizationManager
├── configuration/
│   └── SecurityConfig.java                   # Security configuration
├── controller/
│   └── ResourceController.java               # REST endpoints
├── services/
│   ├── Policy.java                           # Custom @Policy annotation
│   ├── PolicyService.java                    # Service with policy methods
│   └── PolicyType.java                       # Policy type enum (OPEN/RESTRICTED)
└── user/
    └── ContextUser.java                      # Custom UserDetails implementation
```

## How It Works

### Custom @Policy Annotation

Methods can be annotated with `@Policy` to define access rules:

```java
@Policy(PolicyType.OPEN)
public String openPolicy() {
    return "Open Policy Service";
}

@Policy(PolicyType.RESTRICTED)
public String restrictedPolicy() {
    return "Restricted Policy Service";
}
```

### Authorization Logic

The `AuthorizationManagerImpl` evaluates access based on:

1. **Authentication Status** - User must be authenticated (not anonymous)
2. **Policy Type**:
   - `OPEN` - Accessible to all authenticated users
   - `RESTRICTED` - Only accessible to users with `accessToRestrictedPolicy` flag

```java
@Override
public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation methodInvocation) {
    Policy policyAnnotation = AnnotationUtils.findAnnotation(methodInvocation.getMethod(), Policy.class);
    ContextUser contextUser = (ContextUser) authentication.get().getPrincipal();
    
    return new AuthorizationDecision(Optional.ofNullable(policyAnnotation)
        .map(Policy::value)
        .filter(policy -> policy == PolicyType.OPEN || 
                (policy == PolicyType.RESTRICTED && contextUser.hasAccessToRestrictedPolicy()))
        .isPresent());
}
```

### Pre-configured Users

| Username | Password | Restricted Access | Roles |
|----------|----------|-------------------|-------|
| user | userPass | ❌ No | USER |
| admin | adminPass | ✅ Yes | ADMIN, USER |

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
java -jar target/enable-method-security.jar
```

The application will start on **http://localhost:8080**

## API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/openPolicy` | Open policy resource | All authenticated users |
| GET | `/restrictedPolicy` | Restricted policy resource | Users with restricted access only |

## Usage Examples

### Access Open Policy (Any Authenticated User)

```bash
curl -u user:userPass http://localhost:8080/openPolicy
```

Response:
```
Open Policy Service
```

### Access Restricted Policy (Admin Only)

```bash
# ✅ This will succeed (admin has restricted access)
curl -u admin:adminPass http://localhost:8080/restrictedPolicy
```

Response:
```
Restricted Policy Service
```

```bash
# ❌ This will fail (user does not have restricted access)
curl -u user:userPass http://localhost:8080/restrictedPolicy
```

Response:
```json
{"timestamp":...,"status":403,"error":"Forbidden","path":"/restrictedPolicy"}
```

## Security Configuration

This project demonstrates:

1. **@EnableMethodSecurity** - Enables method-level security annotations
2. **Custom AuthorizationManager** - Implements custom authorization logic based on `@Policy` annotation
3. **AOP-based Method Interception** - Uses `AuthorizationManagerBeforeMethodInterceptor` with regex pattern
4. **Lambda DSL** - Modern Spring Security 6.x configuration style
5. **Custom UserDetails** - `ContextUser` with additional `accessToRestrictedPolicy` flag

### Key Configuration

```java
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    
    @Bean
    public AuthorizationManager<MethodInvocation> authorizationManager() {
        return new AuthorizationManagerImpl<>();
    }
    
    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public Advisor authorizationManagerBeforeMethodInterception(
        AuthorizationManager<MethodInvocation> authorizationManager) {
        JdkRegexpMethodPointcut pattern = new JdkRegexpMethodPointcut();
        pattern.setPattern("com.rslakra.enablemethodsecurity.services.*");
        return new AuthorizationManagerBeforeMethodInterceptor(pattern, authorizationManager);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(httpBasic -> {});
        return http.build();
    }
}
```

### Custom UserDetails Implementation

```java
public class ContextUser implements UserDetails {
    private String userName;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;
    private boolean accessToRestrictedPolicy;  // Custom flag for policy access
    
    // Account status flags must be true for authentication to succeed
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    
    public boolean hasAccessToRestrictedPolicy() {
        return accessToRestrictedPolicy;
    }
}
```

## Testing

Run integration tests:

```bash
mvn test
```

## References

- [REST With Spring](http://github.learnspringsecurity.com)
- [Spring @EnableMethodSecurity Annotation](https://www.baeldung.com/spring-enablemethodsecurity)
- [Spring Security Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)

## Author

**Rohtash Lakra**
