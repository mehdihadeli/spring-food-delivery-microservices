package com.github.mehdihadeli.buildingblocks.security;

public interface ClientTokenAccessor {
    /**
     * Get a token from Keycloak with the specified scope and additional configurations.
     */
    ClientTokenAccessorImpl.TokenResponse getClientToken();

    ClientTokenAccessorImpl.TokenResponse generateClientToken();

    /**
     * Check if the current token is expired.
     */
    boolean isTokenExpired();
}
