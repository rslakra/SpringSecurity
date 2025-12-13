package com.rslaka.springsecurity.oauth2.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OAuth2 clients.
 */
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthClientProperties {

    private ClientConfig client = new ClientConfig();
    private ClientConfig fooClient = new ClientConfig();
    private TokenConfig token = new TokenConfig();

    public ClientConfig getClient() {
        return client;
    }

    public void setClient(ClientConfig client) {
        this.client = client;
    }

    public ClientConfig getFooClient() {
        return fooClient;
    }

    public void setFooClient(ClientConfig fooClient) {
        this.fooClient = fooClient;
    }

    public TokenConfig getToken() {
        return token;
    }

    public void setToken(TokenConfig token) {
        this.token = token;
    }

    /**
     * OAuth client configuration.
     */
    public static class ClientConfig {
        private String id;
        private String secret;
        private List<String> redirectUris;
        private List<String> scopes;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public List<String> getRedirectUris() {
            return redirectUris;
        }

        public void setRedirectUris(List<String> redirectUris) {
            this.redirectUris = redirectUris;
        }

        public List<String> getScopes() {
            return scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = scopes;
        }
    }

    /**
     * Token configuration.
     */
    public static class TokenConfig {
        private int accessTokenTtlMinutes = 3;
        private int refreshTokenTtlMinutes = 10;

        public int getAccessTokenTtlMinutes() {
            return accessTokenTtlMinutes;
        }

        public void setAccessTokenTtlMinutes(int accessTokenTtlMinutes) {
            this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        }

        public int getRefreshTokenTtlMinutes() {
            return refreshTokenTtlMinutes;
        }

        public void setRefreshTokenTtlMinutes(int refreshTokenTtlMinutes) {
            this.refreshTokenTtlMinutes = refreshTokenTtlMinutes;
        }
    }
}

