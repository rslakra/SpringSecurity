package com.rslaka.springsecurity.oauth2.configuration;

import com.rslaka.springsecurity.oauth2.model.UserInfo;
import com.rslaka.springsecurity.oauth2.repository.UserInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Initializes default users for the OAuth authorization server.
 */
@Configuration
public class DataInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if users already exist
            if (userInfoRepository.count() == 0) {
                LOGGER.info("Initializing default users...");

                // Create admin user
                UserInfo admin = new UserInfo();
                admin.setUserName("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                admin.setEnabled((short) 1);
                userInfoRepository.save(admin);
                LOGGER.info("Created user: admin / admin123 (ROLE_ADMIN)");

                // Create regular user
                UserInfo user = new UserInfo();
                user.setUserName("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole("ROLE_USER");
                user.setEnabled((short) 1);
                userInfoRepository.save(user);
                LOGGER.info("Created user: user / user123 (ROLE_USER)");

                LOGGER.info("Default users initialized successfully.");
            } else {
                LOGGER.info("Users already exist, skipping initialization.");
            }
        };
    }
}

