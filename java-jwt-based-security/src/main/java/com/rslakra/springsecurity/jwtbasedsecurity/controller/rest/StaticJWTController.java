package com.rslakra.springsecurity.jwtbasedsecurity.controller.rest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.rslakra.springsecurity.jwtbasedsecurity.controller.BaseController;
import com.rslakra.springsecurity.jwtbasedsecurity.model.JwtResponse;
import com.rslakra.springsecurity.jwtbasedsecurity.service.SecretsService;
import com.rslakra.springsecurity.jwtbasedsecurity.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

@RestController
public class StaticJWTController extends BaseController {

    private final SecretsService secretsService;

    /**
     * @param secretsService the secrets service
     */
    @Autowired
    public StaticJWTController(SecretsService secretsService) {
        this.secretsService = secretsService;
    }

    @RequestMapping(value = "/static-builder", method = GET)
    public JwtResponse fixedBuilder() {
        String jws = Jwts.builder()
            .issuer("Rohtash Lakra")
            .subject("rslakra")
            .claim("name", "Rohtash Lakra")
            .claim("scope", "admin")
            .issuedAt(Date.from(Instant.ofEpochSecond(1466796822L))) // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
            .expiration(Date.from(Instant.ofEpochSecond(4622470422L))) // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
            .signWith(secretsService.getHS256SecretKey())
            .compact();

        return new JwtResponse(jws);
    }

    @RequestMapping(value = "/parser", method = GET)
    public JwtResponse parser(@RequestParam String jwt) {
        Jws<Claims> jws = JWTUtils.parseJWTToken(jwt, secretsService.getHS256SecretKey());
        return new JwtResponse(jws);
    }

    @RequestMapping(value = "/parser-enforce", method = GET)
    public JwtResponse parserEnforce(@RequestParam String jwt) {
        Jws<Claims> jws = Jwts.parser()
            .requireIssuer("Rohtash Lakra")
            .require("hasAutomobile", true)
            .verifyWith(secretsService.getHS256SecretKey())
            .build()
            .parseSignedClaims(jwt);

        return new JwtResponse(jws);
    }
}
