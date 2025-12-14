package com.rslakra.jwtauthentication4.config;

import com.rslakra.jwtauthentication4.domain.User;
import com.rslakra.jwtauthentication4.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * @author Rohtash Lakra
 * @created 5/19/20 10:57 AM
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    private static final Logger logger = LoggerFactory.getLogger(JpaConfig.class);
    private final UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param userRepository
     */
    public JpaConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return
     */
    @Bean
    public AuditorAware<User> auditor() {
        return () -> {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()) {
                    return Optional.empty();
                }
                
                Object principal = authentication.getPrincipal();
                if (!(principal instanceof UserDetails)) {
                    return Optional.empty();
                }
                
                UserDetails userDetails = (UserDetails) principal;
                String username = userDetails.getUsername();
                
                // Fetch user from repository - should return managed entity within transaction
                try {
                    Optional<User> dbUser = userRepository.findByUsername(username);
                    return dbUser;
                } catch (Exception e) {
                    logger.debug("Error fetching user {} for auditing: {}", username, e.getMessage());
                    return Optional.empty();
                }
            } catch (Exception e) {
                logger.debug("Error getting current auditor: {}", e.getMessage());
                // Return empty on any error - allows entities to be saved without auditing
                return Optional.empty();
            }
        };
    }
}
