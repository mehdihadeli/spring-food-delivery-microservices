package com.github.mehdihadeli.buildingblocks.security;

import org.keycloak.admin.client.Keycloak;

public interface KeycloakCustomClientFactory {
    Keycloak createClient();

    Keycloak createClient(OAthCustomClientOptions options);
}
