package com.rslakra.springsecurity.config;

import com.rslakra.springsecurity.filter.PasswordAuthenticationFilter;
import com.rslakra.springsecurity.service.UserDetailService;
import com.rslakra.springsecurity.service.UserDetailsAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationProvider authProvider() {
        UserDetailsAuthenticationProvider provider
            = new UserDetailsAuthenticationProvider(passwordEncoder, userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/index", "/login", "/login.html")
                .permitAll()
                .requestMatchers("/user/**")
                .authenticated()
                .anyRequest()
                .authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll()
            )
            .addFilterBefore(authenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public PasswordAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager)
        throws Exception {
        PasswordAuthenticationFilter filter = new PasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationFailureHandler(failureHandler());
        return filter;
    }

    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }

}
