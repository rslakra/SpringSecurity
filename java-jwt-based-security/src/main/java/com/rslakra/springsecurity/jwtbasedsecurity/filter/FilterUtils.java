package com.rslakra.springsecurity.jwtbasedsecurity.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.Objects;

/**
 * @author Rohtash Lakra
 * @created 5/1/23 5:22 PM
 */
public enum FilterUtils {
    INSTANCE;
    public static final String KEY_CSRF = "_csrf";
    public static final String KEY_POST = "POST";

    /**
     * @param servletRequest the HTTP request
     * @param attrName       the attribute name
     * @param responseType   the expected type
     * @param <T>            the type parameter
     * @return the attribute value
     */
    public static <T> T getAttribute(HttpServletRequest servletRequest, String attrName, Class<T> responseType) {
        if (Objects.nonNull(servletRequest) && Objects.nonNull(attrName)) {
            Object result = servletRequest.getAttribute(attrName);
            if (Objects.nonNull(result) && responseType.isAssignableFrom(result.getClass())) {
                return responseType.cast(result);
            }
        }

        return null;
    }

    /**
     * @param servletRequest the HTTP request
     * @param attrName       the attribute name
     * @return the attribute value as String
     */
    public static String getAttribute(HttpServletRequest servletRequest, String attrName) {
        return getAttribute(servletRequest, attrName, String.class);
    }

    /**
     * @param servletRequest the HTTP request
     * @return the CSRF token
     */
    public static CsrfToken getCsrfAttribute(HttpServletRequest servletRequest) {
        return getAttribute(servletRequest, KEY_CSRF, CsrfToken.class);
    }

    /**
     * @param servletRequest the HTTP request
     * @return true if POST request
     */
    public static boolean isPostRequest(HttpServletRequest servletRequest) {
        return (Objects.nonNull(servletRequest) && KEY_POST.equalsIgnoreCase(servletRequest.getMethod()));
    }
}
