package com.rslakra.springsecurity.jwtbasedsecurity.controller.rest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.rslakra.springsecurity.jwtbasedsecurity.controller.BaseController;
import com.rslakra.springsecurity.jwtbasedsecurity.model.JwtResponse;
import com.rslakra.springsecurity.jwtbasedsecurity.service.SecretsService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RestController
public class DynamicJWTController extends BaseController {

    private final SecretsService secretsService;

    /**
     * @param secretsService the secrets service
     */
    @Autowired
    public DynamicJWTController(SecretsService secretsService) {
        this.secretsService = secretsService;
    }

    /**
     * @param claims the claims map
     * @return JwtResponse
     */
    @RequestMapping(value = "/dynamic-builder-general", method = POST)
    public JwtResponse dynamicBuilderGeneric(@RequestBody Map<String, Object> claims) {
        String jws = Jwts.builder()
            .claims(claims)
            .signWith(secretsService.getHS256SecretKey())
            .compact();
        return new JwtResponse(jws);
    }

    /**
     * @param claims the claims map
     * @return JwtResponse with compressed token
     */
    @RequestMapping(value = "/dynamic-builder-compress", method = POST)
    public JwtResponse dynamicBuilderCompress(@RequestBody Map<String, Object> claims) {
        String jws = Jwts.builder()
            .claims(claims)
            .compressWith(Jwts.ZIP.DEF)
            .signWith(secretsService.getHS256SecretKey())
            .compact();
        return new JwtResponse(jws);
    }

    /**
     * @param claims the claims map
     * @return JwtResponse with specific claims
     */
    @RequestMapping(value = "/dynamic-builder-specific", method = POST)
    public JwtResponse dynamicBuilderSpecific(@RequestBody Map<String, Object> claims) {
        var builder = Jwts.builder();

        claims.forEach((key, value) -> {
            switch (key) {
                case "iss":
                    ensureType(key, value, String.class);
                    builder.issuer((String) value);
                    break;
                case "sub":
                    ensureType(key, value, String.class);
                    builder.subject((String) value);
                    break;
                case "aud":
                    ensureType(key, value, String.class);
                    builder.audience().add((String) value);
                    break;
                case "exp":
                    ensureType(key, value, Long.class);
                    builder.expiration(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case "nbf":
                    ensureType(key, value, Long.class);
                    builder.notBefore(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case "iat":
                    ensureType(key, value, Long.class);
                    builder.issuedAt(Date.from(Instant.ofEpochSecond(Long.parseLong(value.toString()))));
                    break;
                case "jti":
                    ensureType(key, value, String.class);
                    builder.id((String) value);
                    break;
                default:
                    builder.claim(key, value);
            }
        });

        builder.signWith(secretsService.getHS256SecretKey());

        return new JwtResponse(builder.compact());
    }

    /**
     * @param registeredClaim the claim name
     * @param value           the claim value
     * @param expectedType    the expected type
     */
    private void ensureType(String registeredClaim, Object value, Class<?> expectedType) {
        boolean isCorrectType =
            expectedType.isInstance(value) || expectedType == Long.class && value instanceof Integer;

        if (!isCorrectType) {
            String msg =
                "Expected type: " + expectedType.getCanonicalName() + " for registered claim: '" + registeredClaim
                + "', but got value: " + value + " of type: " + value.getClass().getCanonicalName();
            throw new JwtException(msg);
        }
    }
}
