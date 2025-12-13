package com.rslakra.jwtauthentication1.security;

import com.rslakra.jwtauthentication1.security.jwt.AuthTokenFilter;
import com.rslakra.jwtauthentication1.security.jwt.JwtAuthenticationEntryPoint;
import com.rslakra.jwtauthentication1.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final com.rslakra.jwtauthentication1.security.jwt.JwtUtils jwtUtils;

    /**
     * @param userDetailsService
     * @param authEntryPoint
     * @param jwtUtils
     */
    public WebSecurityConfig(final UserDetailsServiceImpl userDetailsService,
                             final JwtAuthenticationEntryPoint authEntryPoint,
                             final com.rslakra.jwtauthentication1.security.jwt.JwtUtils jwtUtils) {
        logger.debug("WebSecurityConfig({}, {}, {})", userDetailsService, authEntryPoint, jwtUtils);
        this.userDetailsService = userDetailsService;
        this.authEntryPoint = authEntryPoint;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authJwtTokenFilter() {
        logger.debug("authJwtTokenFilter()");
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        logger.debug("authenticationManager({})", authConfig);
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("passwordEncoder()");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.debug("filterChain({})", http);
        http
            .authenticationProvider(authenticationProvider())
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/api/home/**").permitAll()
                .anyRequest().authenticated()
            );
        http.addFilterBefore(authJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
