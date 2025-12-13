# Spring Security Filter Chain

A Spring Boot application demonstrating the `SecurityFilterChain` approach for configuring Spring Security (replacing the deprecated `WebSecurityConfigurerAdapter`).

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Spring Security | 6.x |
| Thymeleaf | (managed by Spring Boot) |
| Bootstrap | 5.3.5 |
| jQuery | 3.7.1 |
| H2 Database | (managed by Spring Boot) |

## Project Structure

```
spring-security-filter-chain/
├── src/main/java/com/rslakra/securityfilterchain/
│   ├── SecurityFilterChainApplication.java   # Main application class
│   ├── configuration/
│   │   ├── SecurityConfig.java               # Security filter chain configuration
│   │   └── UserDetailServiceConfig.java      # In-memory user details
│   └── controller/
│       └── ResourceController.java           # Web endpoints
├── src/main/resources/
│   ├── application.properties
│   ├── logback.xml
│   ├── static/css/styles.css
│   └── templates/                            # Thymeleaf templates
├── src/test/
│   └── java/.../SecurityFilterChainIntegrationTest.java
├── pom.xml
└── buildMaven.sh
```

## Build & Run

### Build
```bash
./buildMaven.sh
```

### Run
```bash
./runMaven.sh
# or
java -jar target/spring-security-filter-chain.jar
```

The application runs at: **http://localhost:8080**

## Default Users

| Username | Password | Roles |
|----------|----------|-------|
| `user` | `userPass` | USER |
| `admin` | `adminPass` | ADMIN, USER |

## Endpoints

| Endpoint | Access | Description |
|----------|--------|-------------|
| `/`, `/index` | Public | Home page |
| `/login` | Public | Login page |
| `/about-us` | Public | About us page |
| `/contact-us` | Public | Contact page |
| `/register`, `/registration` | Public | User registration pages |
| `/user` | USER, ADMIN | User dashboard |
| `/admin` | ADMIN only | Admin panel |
| `/all` | Authenticated | All roles endpoint |
| `/logout` | Authenticated | Logout endpoint |
| `DELETE /delete` | ADMIN only | Delete endpoint |

## Security Configuration

The project uses the modern `SecurityFilterChain` approach with explicit authentication provider configuration:

### User Details Configuration

Users are configured in `UserDetailServiceConfig` with BCrypt password encoding:

```java
@Configuration
public class UserDetailServiceConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(User.withUsername("user")
                                          .password(passwordEncoder.encode("userPass"))
                                          .roles("USER")
                                          .build());
        userDetailsManager.createUser(User.withUsername("admin")
                                          .password(passwordEncoder.encode("adminPass"))
                                          .roles("ADMIN", "USER")
                                          .build());
        return userDetailsManager;
    }
}
```

### Security Filter Chain Configuration

The main security configuration includes:

1. **Authentication Provider**: Explicitly configured `DaoAuthenticationProvider` to use `UserDetailsService` and `PasswordEncoder`
2. **Custom Entry Point**: Returns 401 for API requests, redirects browsers to login page
3. **Form Login**: Custom login page with error handling
4. **Logout**: Session invalidation and cookie cleanup

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                          DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Public pages
                .requestMatchers("/", "/index", "/login", "/login/**").permitAll()
                .requestMatchers("/about-us", "/contact-us").permitAll()
                .requestMatchers("/register", "/registration").permitAll()
                .requestMatchers("/error/**").permitAll()
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                // Admin only
                .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // User or Admin
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                // Return 401 for API requests, redirect to login for browser requests
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint("/login"))
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/user", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }
}
```

### Custom Authentication Entry Point

The `CustomAuthenticationEntryPoint` intelligently handles different request types:
- **API Requests** (no `Accept: text/html` header): Returns HTTP 401 Unauthorized
- **Browser Requests** (with `Accept: text/html` header): Redirects to `/login` page

This allows the application to work seamlessly with both web browsers and REST API clients.

### Key Changes from WebSecurityConfigurerAdapter

| Old (Deprecated) | New (SecurityFilterChain) |
|------------------|---------------------------|
| `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` |
| `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| `.antMatchers()` | `.requestMatchers()` |
| `.authorizeRequests()` | `.authorizeHttpRequests()` |
| Method chaining with `.and()` | Lambda DSL |

## Testing

Run the integration tests:
```bash
mvn test -Drevision=0.0.1-SNAPSHOT
```

The `SecurityFilterChainIntegrationTest` verifies:
- Public endpoints are accessible without authentication
- Protected endpoints require authentication (returns 401 for API requests)
- Role-based access control works correctly
- Admin-only endpoints are properly secured
- User endpoints are accessible to both USER and ADMIN roles

## cURL Examples

### Access public endpoint (no auth)
```bash
curl http://localhost:8080/
```

### Access protected endpoint without auth (returns 401)
```bash
curl http://localhost:8080/user
# Returns: HTTP/1.1 401 Unauthorized
```

### Access user endpoint with authentication
```bash
curl -u user:userPass http://localhost:8080/user
```

### Access admin endpoint
```bash
curl -u admin:adminPass http://localhost:8080/admin
```

### Delete endpoint (admin only)
```bash
curl -X DELETE -u admin:adminPass \
  -H "Content-Type: text/plain" \
  -d "test-resource" \
  http://localhost:8080/delete
```

### Test API vs Browser behavior
```bash
# API request (no Accept header) - returns 401
curl http://localhost:8080/all

# Browser-like request (with Accept header) - redirects to login
curl -H "Accept: text/html" http://localhost:8080/all -L
```

## Security Filter Chain Order

When the application starts, Spring Security logs the filter chain:

```
Security filter chain: [
  DisableEncodeUrlFilter
  WebAsyncManagerIntegrationFilter
  SecurityContextHolderFilter
  HeaderWriterFilter
  LogoutFilter
  UsernamePasswordAuthenticationFilter
  RequestCacheAwareFilter
  SecurityContextHolderAwareRequestFilter
  AnonymousAuthenticationFilter
  ExceptionTranslationFilter
  AuthorizationFilter
]
```

## Authentication Flow

1. **User Registration**: Users can register via `/register` or `/registration` endpoints (public access)
2. **Login**: Users authenticate via `/login` page using form-based authentication
3. **Password Encoding**: All passwords are encoded using BCrypt before storage and verification
4. **Session Management**: Successful login creates a session; logout invalidates the session and clears cookies
5. **API vs Browser**: 
   - Browser requests to protected endpoints redirect to `/login`
   - API requests (without `Accept: text/html`) return HTTP 401 Unauthorized

## Relevant Articles

- [Spring Security: Upgrading the Deprecated WebSecurityConfigurerAdapter](https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter)
- [Spring Security Lambda DSL](https://spring.io/blog/2019/11/21/spring-security-lambda-dsl)



## Author

**Rohtash Lakra**

