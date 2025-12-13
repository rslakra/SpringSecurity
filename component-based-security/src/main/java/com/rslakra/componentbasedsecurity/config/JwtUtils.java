package com.rslakra.componentbasedsecurity.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    @Value("${app.jwtSecret}")
    private String secret;

    @Value("${app.jwtExpirationInMillis}")
    private Long jwtExpirationInMillis;

    /**
     * Returns the secret key for signing JWT tokens.
     *
     * @return SecretKey
     */
    private SecretKey getSigningKey() {
        // Ensure the secret is at least 256 bits (32 bytes) for HS256
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // Pad the key if it's too short
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            keyBytes = paddedKey;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates the JWT token.
     * <p>
     * While creating the token -
     * <pre>
     *      1. Define claims of the token, like Issuer, Expiration, Subject, and the ID.
     *      2. Sign the JWT using the HS256 algorithm and secret key.
     *      3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
     *          compaction of the JWT to a URL-safe string
     * </pre>
     *
     * @param claims  the claims
     * @param subject the subject
     * @return the JWT token
     */
    public String generateToken(final Map<String, Object> claims, final String subject) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(getSigningKey())
            .compact();
    }

    /**
     * Generates token for user.
     *
     * @param userDetails the user details
     * @return the JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        final Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, userDetails.getUsername());
    }

    /**
     * Returns the Claims extracted from the token.
     * <p>
     * To retrieve any information from token we will need the secret key.
     *
     * @param token the JWT token
     * @return the claims
     */
    public Claims extractAllClaimsFromToken(final String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Returns the Claims extracted from the token.
     *
     * @param token          the JWT token
     * @param claimsResolver the claims resolver function
     * @param <T>            the type of the claim
     * @return the resolved claim
     */
    public <T> T extractClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Returns the username extracted from the JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUserNameFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Returns the expiration date extracted from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Returns true if the token is valid otherwise false.
     *
     * @param token       the JWT token
     * @param userDetails the user details
     * @return true if valid, false otherwise
     */
    public boolean isValidToken(final String token, final UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Returns true if the token is already expired otherwise false.
     *
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
