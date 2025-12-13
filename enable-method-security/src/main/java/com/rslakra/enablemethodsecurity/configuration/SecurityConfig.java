package com.rslakra.enablemethodsecurity.configuration;

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import com.rslakra.enablemethodsecurity.authentication.UserDetailsServiceImpl;
import com.rslakra.enablemethodsecurity.authorization.AuthorizationManagerImpl;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    /**
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * @param httpSecurity HttpSecurity
     * @param userDetailsService UserDetailsService
     * @param passwordEncoder PasswordEncoder
     * @return AuthenticationManager
     * @throws Exception if error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity,
                                                        UserDetailsService userDetailsService,
                                                        PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
            httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authManagerBuilder.build();
    }

    /**
     * @param passwordEncoder PasswordEncoder
     * @return UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new UserDetailsServiceImpl((BCryptPasswordEncoder) passwordEncoder);
    }

    /**
     * @return AuthorizationManager
     */
    @Bean
    public AuthorizationManager<MethodInvocation> authorizationManager() {
        return new AuthorizationManagerImpl<>();
    }

    /**
     * @param authorizationManager AuthorizationManager
     * @return Advisor
     */
    @Bean
    @Role(ROLE_INFRASTRUCTURE)
    public Advisor authorizationManagerBeforeMethodInterception(
        AuthorizationManager<MethodInvocation> authorizationManager) {
        JdkRegexpMethodPointcut pattern = new JdkRegexpMethodPointcut();
        pattern.setPattern("com.rslakra.enablemethodsecurity.services.*");
        return new AuthorizationManagerBeforeMethodInterceptor(pattern, authorizationManager);
    }

    /**
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception if error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            // Use stateless session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Enable HTTP Basic authentication
            .httpBasic(httpBasic -> {});

        return http.build();
    }
}
