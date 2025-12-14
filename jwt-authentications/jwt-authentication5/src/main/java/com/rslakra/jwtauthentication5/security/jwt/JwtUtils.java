package com.rslakra.jwtauthentication5.security.jwt;

import com.rslakra.jwtauthentication5.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMillis}")
    private int jwtExpirationMs;

    private SecretKey secretKey;

    @jakarta.annotation.PostConstruct
    protected void init() {
        // For HS512, we need at least 512 bits (64 bytes)
        String secret = jwtSecret;
        if (secret.length() < 64) {
            int repeatCount = (64 / secret.length()) + 1;
            secret = secret.repeat(repeatCount).substring(0, 64);
        }
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * @param jwtExpirationInMinutes
     * @return
     */
    public static Long getExpiryTime(int jwtExpirationInMinutes) {
        return (new Date().getTime() + jwtExpirationInMinutes * 10000);
    }

    /**
     * @param authentication
     * @return
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        Date now = new Date();
        Date expiry = new Date(getExpiryTime(jwtExpirationMs));
        return Jwts.builder()
            .subject(userPrincipal.getUsername())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey)
            .compact();
    }

    /**
     * @param token
     * @return
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    /**
     * @param authToken
     * @return
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            LOGGER.error("JWT error: {}", e.getMessage());
        }

        return false;
    }
}
