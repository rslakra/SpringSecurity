package com.rslakra.jwtauthentication4.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    private static Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    private UserDetailsService userDetailsService;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        logger.debug("init()");
        // For HS256, we need at least 256 bits (32 bytes)
        String secret = jwtProperties.getSecretKey();
        // Ensure minimum length for HS256 (32 bytes = 256 bits)
        // If shorter, repeat the string to reach minimum length
        if (secret.length() < 32) {
            int repeatCount = (32 / secret.length()) + 1;
            secret = secret.repeat(repeatCount).substring(0, 32);
        }
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * @param username
     * @param roles
     * @return
     */
    public String createToken(String username, List<String> roles) {
        logger.debug("createToken({}, {})", username, roles);
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtProperties.getValidityInMillis());
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }

    /**
     * @param token
     * @return
     */
    public Authentication getAuthentication(final String token) {
        logger.debug("Authentication({})", token);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * @param token
     * @return
     */
    public String getUsername(final String token) {
        logger.debug("getUsername({})", token);
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    /**
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        logger.debug("resolveToken({})", request);
        final String accessToken = request.getHeader(AUTHORIZATION);
        if (accessToken != null && accessToken.startsWith(BEARER)) {
            return accessToken.substring(7, accessToken.length());
        }

        return null;
    }

    /**
     * @param token
     * @return
     */
    public boolean hasValidToken(final String token) {
        logger.debug("+hasValidToken({})", token);
        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            if (claims.getExpiration().before(new Date())) {
                logger.debug("-hasValidToken(), result:false");
                return false;
            }

            logger.debug("-hasValidToken(), result:true");
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            logger.debug("-hasValidToken(), result:false");
            throw new InvalidJwtAuthenticationException("Invalid JWT Token!", ex);
        }
    }

}
