package com.rslakra.securityfilterchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Security Filter Chain Application.
 * Demonstrates SecurityFilterChain configuration with Spring Security 6.x.
 */
@SpringBootApplication
public class SecurityFilterChainApplication {

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SecurityFilterChainApplication.class, args);
    }
}
