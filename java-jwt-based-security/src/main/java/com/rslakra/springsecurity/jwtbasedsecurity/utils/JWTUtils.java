package com.rslakra.springsecurity.jwtbasedsecurity.utils;

import com.rslakra.springsecurity.jwtbasedsecurity.model.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public enum JWTUtils {
    INSTANCE;
    public static final String NEW_LINE = "\n";
    public static final String TAB = "\t";
    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String AUDIENCE = "aud";
    public static final String EXPIRATION = "exp";
    public static final String NOT_BEFORE = "nbf";
    public static final String ISSUED_AT = "iat";
    public static final String JWT_ID = "jti";

    /**
     * @param servletRequest the HTTP request
     * @return true if port is 80
     */
    public static boolean isPort80(HttpServletRequest servletRequest) {
        return (Objects.nonNull(servletRequest) && servletRequest.getServerPort() == 80);
    }

    /**
     * @param servletRequest the HTTP request
     * @return true if port is 443
     */
    public static boolean isPort443(HttpServletRequest servletRequest) {
        return (Objects.nonNull(servletRequest) && servletRequest.getServerPort() == 443);
    }

    /**
     * @param servletRequest the HTTP request
     * @return the request URL
     */
    public static String getRequestUrl(HttpServletRequest servletRequest) {
        return servletRequest.getScheme()
               + "://" + servletRequest.getServerName()
               + ((isPort80(servletRequest) || isPort443(servletRequest)) ? "" : ":" + servletRequest.getServerPort());
    }

    /**
     * Creates a SecretKey from byte array
     *
     * @param secretBytes the secret bytes
     * @return SecretKey
     */
    private static SecretKey getSecretKey(byte[] secretBytes) {
        // Ensure key is at least 256 bits for HS256
        if (secretBytes.length < 32) {
            byte[] paddedKey = new byte[32];
            System.arraycopy(secretBytes, 0, paddedKey, 0, secretBytes.length);
            secretBytes = paddedKey;
        }
        return Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * @param claims      the claims map
     * @param secretBytes the secret bytes
     * @return the JWT compact string
     */
    public static String jwtCompactBuilderWithClaims(final Map<String, Object> claims, final byte[] secretBytes) {
        var builder = Jwts.builder();
        
        claims.forEach((key, value) -> {
            switch (key) {
                case ISSUER:
                    INSTANCE.assertClaimType(key, value, String.class);
                    builder.issuer((String) value);
                    break;
                case SUBJECT:
                    INSTANCE.assertClaimType(key, value, String.class);
                    builder.subject((String) value);
                    break;
                case AUDIENCE:
                    INSTANCE.assertClaimType(key, value, String.class);
                    builder.audience().add((String) value);
                    break;
                case EXPIRATION:
                    INSTANCE.assertClaimType(key, value, Long.class);
                    builder.expiration(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case NOT_BEFORE:
                    INSTANCE.assertClaimType(key, value, Long.class);
                    builder.notBefore(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case ISSUED_AT:
                    INSTANCE.assertClaimType(key, value, Long.class);
                    builder.issuedAt(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case JWT_ID:
                    INSTANCE.assertClaimType(key, value, String.class);
                    builder.id((String) value);
                    break;
                default:
                    builder.claim(key, value);
            }
        });

        builder.signWith(getSecretKey(secretBytes));
        return builder.compact();
    }

    /**
     * @param claims      the claims map
     * @param secretBytes the secret bytes
     * @return JwtResponse
     */
    public static JwtResponse jwtBuilderWithClaims(final Map<String, Object> claims, final byte[] secretBytes) {
        return new JwtResponse(jwtCompactBuilderWithClaims(claims, secretBytes));
    }

    /**
     * @param issuer          the issuer
     * @param subject         the subject
     * @param issuedAtSeconds the issued at time in seconds
     * @param expiryInSeconds the expiry time in seconds
     * @param customClaims    the custom claims
     * @param secretBytes     the secret bytes
     * @return the JWT token
     */
    public static String jwtBuilder(String issuer, String subject, Long issuedAtSeconds, Long expiryInSeconds,
                                    Map<String, Object> customClaims, byte[] secretBytes) {
        var builder = Jwts.builder();
        
        if (Objects.nonNull(issuer)) {
            builder.issuer(issuer);
        }

        if (Objects.nonNull(subject)) {
            builder.subject(subject);
        }

        if (Objects.nonNull(customClaims)) {
            customClaims.forEach(builder::claim);
        }

        if (Objects.nonNull(issuedAtSeconds)) {
            builder.issuedAt(Date.from(Instant.ofEpochSecond(issuedAtSeconds)));
        }

        if (Objects.nonNull(expiryInSeconds)) {
            builder.expiration(Date.from(Instant.ofEpochSecond(expiryInSeconds)));
        }

        builder.signWith(getSecretKey(secretBytes));
        return builder.compact();
    }

    /**
     * @param claimKey   the claim key
     * @param claimValue the claim value
     * @param claimType  the expected type
     */
    private void assertClaimType(String claimKey, Object claimValue, Class<?> claimType) {
        boolean validClaimType =
            claimType.isInstance(claimValue) || claimType == Long.class && claimValue instanceof Integer;
        if (!validClaimType) {
            String errorMessage =
                "Expected type: " + claimType.getCanonicalName() + " for claim: '" + claimKey
                + "', but provided value: " + claimValue + " of type: " + claimValue.getClass().getCanonicalName();
            throw new JwtException(errorMessage);
        }
    }

    /**
     * @param jwtToken  the JWT token
     * @param secretKey the secret key
     * @return the parsed claims
     */
    public static Jws<Claims> parseJWTToken(final String jwtToken, final SecretKey secretKey) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwtToken);
    }

    /**
     * @param token the JWT token
     * @return decoded header and payload
     */
    public static String decodeJWTToken(String token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = token.split("\\.");
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return header + " " + payload;
    }

    /**
     * @param token     the JWT token
     * @param secretKey the secret key string
     * @return decoded and verified header and payload
     * @throws Exception if verification fails
     */
    public static String decodeJWTToken(String token, String secretKey) throws Exception {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] tokenChunks = token.split("\\.");
        String header = new String(decoder.decode(tokenChunks[0]));
        String payload = new String(decoder.decode(tokenChunks[1]));

        // Verify the token
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        } catch (JwtException e) {
            throw new Exception("Could not verify JWT token integrity!", e);
        }

        return header + " " + payload;
    }
}
