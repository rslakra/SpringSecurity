package com.rslakra.springsecurity.jwtbasedsecurity.config;

import com.rslakra.springsecurity.jwtbasedsecurity.filter.JwtCsrfValidatorFilter;
import com.rslakra.springsecurity.jwtbasedsecurity.service.SecretsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final CsrfTokenRepository jwtCsrfTokenRepository;
    private final SecretsService secretsService;

    // ordered so we can use binary search below
    private final String[] ignoreCsrfAntMatchers = {
        "/dynamic-builder-compress", "/dynamic-builder-general", "/dynamic-builder-specific", "/set-secrets"
    };

    public WebSecurityConfig(CsrfTokenRepository jwtCsrfTokenRepository, SecretsService secretsService) {
        this.jwtCsrfTokenRepository = jwtCsrfTokenRepository;
        this.secretsService = secretsService;
    }

    /**
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception if error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
            .addFilterAfter(new JwtCsrfValidatorFilter(secretsService, ignoreCsrfAntMatchers), CsrfFilter.class)
            .csrf(csrf -> csrf
                .csrfTokenRepository(jwtCsrfTokenRepository)
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers(ignoreCsrfAntMatchers)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
            );

        return http.build();
    }
}
