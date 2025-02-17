package com.github.mehdihadeli.buildingblocks.security;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakCustomClientFactoryImpl implements KeycloakCustomClientFactory {

    private final OAthCustomClientOptions keycloakClientOptions;

    public KeycloakCustomClientFactoryImpl(OAthCustomClientOptions keycloakClientOptions) {
        this.keycloakClientOptions = keycloakClientOptions;
    }

    @Override
    public Keycloak createClient() {
        return createClient(this.keycloakClientOptions);
    }

    @Override
    public Keycloak createClient(OAthCustomClientOptions keycloakClientOptions) {
        if (StringUtils.isEmpty(keycloakClientOptions.getHostUrl())) {
            throw new IllegalArgumentException("Keycloak Client URI can not be empty");
        }

        if (StringUtils.isEmpty(keycloakClientOptions.getAdminRealmName())) {
            throw new IllegalArgumentException("Keycloak admin realm name can not be empty");
        }

        if (StringUtils.isEmpty(keycloakClientOptions.getApplicationRealmName())) {
            throw new IllegalArgumentException("Keycloak application realm name can not be empty");
        }

        // Initialize the KeycloakBuilder
        KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder()
                .serverUrl(keycloakClientOptions.getHostUrl())
                // admin realm and admin for managing accounts
                .realm(keycloakClientOptions.getAdminRealmName())
                .clientId(keycloakClientOptions.getClientId());

        String grantType = keycloakClientOptions.getGrantType();
        if (grantType == null) {
            grantType = "client_credentials";
        }
        keycloakBuilder.grantType(grantType);

        if (keycloakClientOptions.getClientSecret() != null) {
            keycloakBuilder.clientSecret(keycloakClientOptions.getClientSecret());
        }

        if (keycloakClientOptions.getUserName() != null) {
            keycloakBuilder.username(keycloakClientOptions.getUserName());
        }

        if (keycloakClientOptions.getPassword() != null) {
            keycloakBuilder.password(keycloakClientOptions.getPassword());
        }

        if (keycloakClientOptions.getScope() != null) {
            keycloakBuilder.scope(keycloakClientOptions.getScope());
        }

        // Build the Keycloak instance
        var keycloak = keycloakBuilder.build();

        return keycloak;
    }
}
