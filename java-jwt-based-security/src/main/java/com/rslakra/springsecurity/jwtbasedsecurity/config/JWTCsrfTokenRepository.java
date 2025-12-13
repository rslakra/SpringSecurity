package com.rslakra.springsecurity.jwtbasedsecurity.config;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

public class JWTCsrfTokenRepository implements CsrfTokenRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTCsrfTokenRepository.class);
    private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = CSRFConfig.class.getName()
        .concat(".CSRF_TOKEN");

    private final SecretKey secretKey;

    /**
     * @param secretKey the secret key for signing
     */
    public JWTCsrfTokenRepository(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * @param request the HTTP request
     * @return the generated CSRF token
     */
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String id = UUID.randomUUID()
            .toString()
            .replace("-", "");

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + (1000 * 30)); // 30 seconds

        String token = Jwts.builder()
            .id(id)
            .issuedAt(now)
            .notBefore(now)
            .expiration(exp)
            .signWith(secretKey)
            .compact();

        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", token);
    }

    /**
     * @param token    the CSRF token
     * @param request  the HTTP request
     * @param response the HTTP response
     */
    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME);
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME, token);
        }
    }

    /**
     * @param request the HTTP request
     * @return the loaded CSRF token
     */
    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || "GET".equals(request.getMethod())) {
            return null;
        }
        return (CsrfToken) session.getAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME);
    }
}
