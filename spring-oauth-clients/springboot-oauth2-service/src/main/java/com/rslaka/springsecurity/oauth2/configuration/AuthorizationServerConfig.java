package com.rslaka.springsecurity.oauth2.configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

/**
 * OAuth2 Authorization Server configuration using Spring Authorization Server.
 * Replaces the deprecated spring-security-oauth2 library.
 */
@Configuration
public class AuthorizationServerConfig {

    private final OAuthClientProperties oAuthClientProperties;

    public AuthorizationServerConfig(OAuthClientProperties oAuthClientProperties) {
        this.oAuthClientProperties = oAuthClientProperties;
    }

    /**
     * Security filter chain for the authorization server endpoints.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
            OAuth2AuthorizationServerConfigurer.authorizationServer();

        http
            .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
            .with(authorizationServerConfigurer, authServer -> authServer
                .oidc(Customizer.withDefaults())
            )
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            )
            .oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }

    /**
     * Registered client repository with in-memory clients.
     * Configuration is loaded from application.properties.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        OAuthClientProperties.ClientConfig clientConfig = oAuthClientProperties.getClient();
        OAuthClientProperties.ClientConfig fooClientConfig = oAuthClientProperties.getFooClient();
        OAuthClientProperties.TokenConfig tokenConfig = oAuthClientProperties.getToken();

        // OAuth client (matches the springboot-oauth-client configuration)
        RegisteredClient.Builder oauthClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientConfig.getId())
            .clientSecret(clientConfig.getSecret())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(tokenConfig.getAccessTokenTtlMinutes()))
                .refreshTokenTimeToLive(Duration.ofMinutes(tokenConfig.getRefreshTokenTtlMinutes()))
                .build())
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .build());

        // Add redirect URIs from config
        if (clientConfig.getRedirectUris() != null) {
            clientConfig.getRedirectUris().forEach(oauthClientBuilder::redirectUri);
        }

        // Add scopes from config
        if (clientConfig.getScopes() != null) {
            clientConfig.getScopes().forEach(oauthClientBuilder::scope);
        }

        RegisteredClient oauthClient = oauthClientBuilder.build();

        // Foo client (legacy client from original configuration)
        RegisteredClient.Builder fooClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(fooClientConfig.getId())
            .clientSecret(fooClientConfig.getSecret())
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(tokenConfig.getAccessTokenTtlMinutes()))
                .refreshTokenTimeToLive(Duration.ofMinutes(tokenConfig.getRefreshTokenTtlMinutes()))
                .build())
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .build());

        // Add redirect URIs from config
        if (fooClientConfig.getRedirectUris() != null) {
            fooClientConfig.getRedirectUris().forEach(fooClientBuilder::redirectUri);
        }

        // Add scopes from config
        if (fooClientConfig.getScopes() != null) {
            fooClientConfig.getScopes().forEach(fooClientBuilder::scope);
        }

        RegisteredClient fooClient = fooClientBuilder.build();

        return new InMemoryRegisteredClientRepository(oauthClient, fooClient);
    }

    /**
     * JWK source for signing JWTs.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration
            .OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
