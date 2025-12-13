package com.rslakra.springsecurity.jwtbasedsecurity.filter;

import com.rslakra.springsecurity.jwtbasedsecurity.service.SecretsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Rohtash Lakra
 * @created 4/25/23 4:04 PM
 */
public final class JwtCsrfValidatorFilter extends OncePerRequestFilter {

    private final SecretsService secretsService;
    private final String[] ignoredCsrfAntMatchers;

    /**
     * @param secretsService         the secrets service
     * @param ignoredCsrfAntMatchers the ignored CSRF matchers
     */
    public JwtCsrfValidatorFilter(SecretsService secretsService, String[] ignoredCsrfAntMatchers) {
        this.secretsService = secretsService;
        this.ignoredCsrfAntMatchers = ignoredCsrfAntMatchers;
    }

    /**
     * @param servletRequest  the HTTP request
     * @param servletResponse the HTTP response
     * @param filterChain     the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                    FilterChain filterChain)
        throws ServletException, IOException {
        /**
         * NOTE: - A real implementation should have a nonce cache so the csrfToken cannot be reused
         */
        CsrfToken csrfToken = FilterUtils.getCsrfAttribute(servletRequest);

        if (
            // only care if it's a POST
            FilterUtils.isPostRequest(servletRequest) &&
            // ignore if the servletRequest path is in our list and we have a csrfToken
            Arrays.binarySearch(ignoredCsrfAntMatchers, servletRequest.getServletPath()) < 0 && csrfToken != null) {
            // CsrfFilter already made sure the csrfToken matched. Here, we'll make sure it's not expired
            try {
                secretsService.parseToken(csrfToken.getToken());
            } catch (JwtException e) {
                // most likely an ExpiredJwtException, but this will handle any
                servletRequest.setAttribute("exception", e);
                servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                RequestDispatcher dispatcher = servletRequest.getRequestDispatcher("expired-jwt");
                dispatcher.forward(servletRequest, servletResponse);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
