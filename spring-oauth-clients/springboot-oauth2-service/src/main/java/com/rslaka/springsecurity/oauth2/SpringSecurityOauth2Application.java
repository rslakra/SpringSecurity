package com.rslaka.springsecurity.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot OAuth2 Authorization Server Application.
 * Uses Spring Authorization Server for OAuth2/OIDC support.
 */
@SpringBootApplication
public class SpringSecurityOauth2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityOauth2Application.class, args);
    }
}
