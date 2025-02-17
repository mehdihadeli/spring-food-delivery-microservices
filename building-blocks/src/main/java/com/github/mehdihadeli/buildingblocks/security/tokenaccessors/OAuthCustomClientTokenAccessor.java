package com.github.mehdihadeli.buildingblocks.security.tokenaccessors;

public interface OAuthCustomClientTokenAccessor {
    /**
     * Get a token from Keycloak with the specified scope and additional configurations.
     */
    OAuthCustomClientTokenAccessorImpl.TokenResponse getClientToken();

    OAuthCustomClientTokenAccessorImpl.TokenResponse generateClientToken();

    /**
     * Check if the current token is expired.
     */
    boolean isTokenExpired();
}
