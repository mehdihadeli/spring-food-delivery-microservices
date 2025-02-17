package com.github.mehdihadeli.buildingblocks.security.tokenaccessors;

import com.github.mehdihadeli.buildingblocks.security.KeycloakCustomClientFactory;
import org.keycloak.admin.client.token.TokenManager;

import java.time.Instant;

public class OAuthCustomClientTokenAccessorImpl implements OAuthCustomClientTokenAccessor {

    private final KeycloakCustomClientFactory keycloakCustomClientFactory;
    private TokenResponse currentToken;

    public OAuthCustomClientTokenAccessorImpl(KeycloakCustomClientFactory keycloakClientFactory) {
        this.keycloakCustomClientFactory = keycloakClientFactory;
    }

    @Override
    public TokenResponse getClientToken() {
        if (currentToken != null && !isTokenExpired()) {
            return currentToken;
        }

        return generateClientToken();
    }

    @Override
    public TokenResponse generateClientToken() {
        // Build the Keycloak instance
        var keycloak = keycloakCustomClientFactory.createClient();

        // Otherwise, use the TokenManager from the Keycloak instance
        TokenManager tokenManager = keycloak.tokenManager();
        var tokenResponse = tokenManager.getAccessToken();

        // Store the token
        if (tokenResponse != null) {
            currentToken = new TokenResponse(
                    tokenResponse.getToken(), tokenResponse.getRefreshToken(), tokenResponse.getExpiresIn());
        } else {
            throw new RuntimeException("Failed to retrieve token from Keycloak");
        }

        return currentToken;
    }

    public boolean isTokenExpired() {
        if (currentToken == null) {
            return true;
        }
        // Calculate the expiration time
        Instant expirationTime = Instant.now().plusSeconds(currentToken.expiresIn());
        return Instant.now().isAfter(expirationTime);
    }

    /**
     * Token response from Keycloak.
     */
    public record TokenResponse(String accessToken, String refreshToken, long expiresIn) {}
}
