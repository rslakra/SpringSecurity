package com.rslakra.springsecurity.jwtbasedsecurity.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rslakra.springsecurity.jwtbasedsecurity.service.SecretsService;
import com.rslakra.springsecurity.jwtbasedsecurity.utils.JWTUtils;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtilsTest {

    private SecretsService secretsService;

    // Secret must be at least 256 bits (32 characters) for HS256
    private static final String VALID_SECRET = "MySecretKeyThatIsAtLeast32BytesLong!";
    private static final String BAD_SECRET = "WrongSecretKeyThatIsAtLeast32Bytes!";

    @BeforeEach
    void setUp() {
        secretsService = new SecretsService();
        secretsService.initObject(); // Initialize secrets manually since not Spring-managed
    }

    /**
     * @param expiryInMinutes the expiry time in minutes
     * @return the claims map
     */
    private static Map<String, Object> buildClaims(long expiryInMinutes) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(JWTUtils.ISSUER, "Rohtash Lakra");
        claims.put(JWTUtils.SUBJECT, "jwtToken");
        claims.put("name", "Rohtash Lakra");
        claims.put("scope", "ADMIN");
        claims.put(JWTUtils.ISSUED_AT, Instant.now().getEpochSecond());
        claims.put(JWTUtils.EXPIRATION, Instant.now().plus(expiryInMinutes, ChronoUnit.MINUTES).getEpochSecond());
        return claims;
    }

    @Test
    public void testGenerateToken() {
        String jwtToken = JWTUtils.jwtCompactBuilderWithClaims(buildClaims(30), secretsService.getHS256SecretBytes());
        assertThat(jwtToken).isNotNull();
        assertThat(jwtToken.split("\\.")).hasSize(3); // Header.Payload.Signature
    }

    @Test
    void givenSimpleToken_whenDecoding_thenStringOfHeaderPayloadAreReturned() {
        // Create a token using our secrets
        String token = Jwts.builder()
            .subject("1234567890")
            .claim("name", "Baeldung User")
            .issuedAt(new Date(1516239022000L))
            .signWith(secretsService.getHS256SecretKey())
            .compact();

        String decoded = JWTUtils.decodeJWTToken(token);
        assertThat(decoded).contains("HS256");
        assertThat(decoded).contains("Baeldung User");
    }

    @Test
    void givenSignedToken_whenDecodingWithInvalidSecret_thenIntegrityIsNotValidated() {
        // Create a token with valid secret
        SecretKey validKey = Jwts.SIG.HS256.key().build();
        String token = Jwts.builder()
            .subject("1234567890")
            .claim("name", "Baeldung User")
            .issuedAt(new Date())
            .signWith(validKey)
            .compact();

        // Try to decode with different secret - should fail
        assertThatThrownBy(() -> JWTUtils.decodeJWTToken(token, BAD_SECRET))
            .hasMessageContaining("Could not verify JWT token integrity!");
    }

    @Test
    void givenSignedToken_whenDecodingWithValidSecret_thenIntegrityIsValidated() throws Exception {
        // Create a token with our valid secret
        String token = Jwts.builder()
            .subject("1234567890")
            .claim("name", "Rohtash Lakra")
            .issuedAt(new Date())
            .signWith(secretsService.getHS256SecretKey())
            .compact();

        // Parse using SecretsService
        var claims = secretsService.parseToken(token);
        assertThat(claims.getSubject()).isEqualTo("1234567890");
        assertThat(claims.get("name")).isEqualTo("Rohtash Lakra");
    }

    @Test
    void testJwtBuilder() {
        String token = JWTUtils.jwtBuilder(
            "Rohtash Lakra",
            "test-subject",
            Instant.now().getEpochSecond(),
            Instant.now().plus(1, ChronoUnit.HOURS).getEpochSecond(),
            Map.of("role", "admin"),
            secretsService.getHS256SecretBytes()
        );

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }
}
