package com.rslakra.componentbasedsecurity.config;

import com.rslakra.componentbasedsecurity.service.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtils jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtRequestFilter(JwtUtils jwtTokenUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param chain       the filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        logger.debug("+doFilterInternal({}, {}, {})", request, response, chain);
        final String requestToken = request.getHeader(JwtUtils.AUTHORIZATION);
        String userName = null;
        String jwtToken = null;
        /**
         * JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
         */
        if (requestToken != null && requestToken.startsWith(JwtUtils.BEARER)) {
            jwtToken = requestToken.substring(7);
            try {
                userName = jwtTokenUtil.getUserNameFromToken(jwtToken);
            } catch (IllegalArgumentException ex) {
                logger.error("Unable to get JWT Token", ex);
            } catch (ExpiredJwtException ex) {
                logger.error("JWT Token has expired", ex);
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            /*
             * if token is valid configure Spring Security to manually set authentication
             */
            if (jwtTokenUtil.isValidToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken
                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                /**
                 * After setting the Authentication in the context,
                 * we specify that the current user is authenticated.
                 * So it passes the Spring Security Configurations successfully.
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        chain.doFilter(request, response);
        logger.debug("-doFilterInternal()");
    }

}
