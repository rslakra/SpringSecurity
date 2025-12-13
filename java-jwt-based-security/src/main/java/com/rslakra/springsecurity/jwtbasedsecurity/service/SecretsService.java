package com.rslakra.springsecurity.jwtbasedsecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecretsService {

    private static final String HS256 = "HS256";
    private static final String HS384 = "HS384";
    private static final String HS512 = "HS512";

    // Store SecretKey objects directly (more reliable than Base64 strings)
    private Map<String, SecretKey> secretKeys = new HashMap<>();

    /**
     * Initialize secrets on startup
     */
    @PostConstruct
    public void initObject() {
        refreshSecrets();
    }

    /**
     * @param algorithm the algorithm name
     * @return SecretKey for the algorithm
     */
    public SecretKey getSecretKey(String algorithm) {
        return secretKeys.get(algorithm);
    }

    /**
     * @return SecretKey for HS256
     */
    public SecretKey getHS256SecretKey() {
        return getSecretKey(HS256);
    }

    /**
     * @return SecretKey for HS384
     */
    public SecretKey getHS384SecretKey() {
        return getSecretKey(HS384);
    }

    /**
     * @return SecretKey for HS512
     */
    public SecretKey getHS512SecretKey() {
        return getSecretKey(HS512);
    }

    /**
     * @return the secrets map as Base64 strings (for API response)
     */
    public Map<String, String> getSecrets() {
        Map<String, String> secrets = new HashMap<>();
        secretKeys.forEach((alg, key) -> 
            secrets.put(alg, Base64.getEncoder().encodeToString(key.getEncoded()))
        );
        return secrets;
    }

    /**
     * @param secrets the secrets to set (Base64-encoded strings)
     */
    public void setSecrets(Map<String, String> secrets) {
        Assert.notNull(secrets, "Secrets cannot be null");
        Assert.hasText(secrets.get(HS256), "HS256 secret is required");
        Assert.hasText(secrets.get(HS384), "HS384 secret is required");
        Assert.hasText(secrets.get(HS512), "HS512 secret is required");

        secrets.forEach((alg, base64Key) -> {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            secretKeys.put(alg, new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256"));
        });
    }

    /**
     * @return byte array for HS256 secret
     */
    public byte[] getHS256SecretBytes() {
        return secretKeys.get(HS256).getEncoded();
    }

    /**
     * @return byte array for HS384 secret
     */
    public byte[] getHS384SecretBytes() {
        return secretKeys.get(HS384).getEncoded();
    }

    /**
     * @return byte array for HS512 secret
     */
    public byte[] getHS512SecretBytes() {
        return secretKeys.get(HS512).getEncoded();
    }

    /**
     * Generate new secrets for all algorithms
     *
     * @return the refreshed secrets map as Base64 strings
     */
    public Map<String, String> refreshSecrets() {
        // Generate keys for each algorithm using JJWT's secure key builder
        secretKeys.put(HS256, Jwts.SIG.HS256.key().build());
        secretKeys.put(HS384, Jwts.SIG.HS384.key().build());
        secretKeys.put(HS512, Jwts.SIG.HS512.key().build());

        return getSecrets();
    }

    /**
     * Parse and verify a JWT token
     *
     * @param token the JWT token
     * @return the Claims
     */
    public Claims parseToken(String token) {
        // Extract algorithm from header to get the right key
        String[] parts = token.split("\\.");
        if (parts.length >= 2) {
            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            String algorithm = HS256; // default
            if (headerJson.contains("HS384")) {
                algorithm = HS384;
            } else if (headerJson.contains("HS512")) {
                algorithm = HS512;
            }

            SecretKey key = getSecretKey(algorithm);
            if (key == null) {
                throw new IllegalStateException("No secret key found for algorithm: " + algorithm);
            }

            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        }
        throw new IllegalArgumentException("Invalid JWT token format");
    }
}
