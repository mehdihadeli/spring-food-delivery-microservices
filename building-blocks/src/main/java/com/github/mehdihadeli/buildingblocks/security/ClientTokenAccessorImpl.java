package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

public class ClientTokenAccessorImpl implements ClientTokenAccessor {

    private TokenResponse currentToken;
    private final KeycloakClientOptions keycloakClientOptions;
    private final RestTemplate restTemplate;

    public ClientTokenAccessorImpl(KeycloakClientOptions keycloakConfig) {
        this.keycloakClientOptions = keycloakConfig;
        this.restTemplate = new RestTemplate();
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
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakClientOptions.getClientId());
        if (keycloakClientOptions.getClientSecret() != null) {
            body.add("client_secret", keycloakClientOptions.getClientSecret());
        }
        if (keycloakClientOptions.getScope() != null) {
            body.add("scope", keycloakClientOptions.getScope());
        }
        if (keycloakClientOptions.getGrantType() != null) {
            body.add("grant_type", keycloakClientOptions.getGrantType());
        }

        // Add additional configurations to the request body
        keycloakClientOptions.getAdditionalConfigs().forEach(body::add);

        // Prepare the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Send the request to Keycloak
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(keycloakClientOptions.getTokenUri(), request, TokenResponse.class);

        // Return the token
        TokenResponse tokenResponse = response.getBody();
        if (tokenResponse != null) {
            currentToken = tokenResponse;
            return tokenResponse;
        } else {
            throw new RuntimeException("Failed to retrieve token from Keycloak");
        }
    }

    public boolean isTokenExpired() {
        if (currentToken == null) {
            return true;
        }
        // Calculate the expiration time
        Instant expirationTime = Instant.now().plusSeconds(currentToken.getExpiresIn());
        return Instant.now().isAfter(expirationTime);
    }

    /**
     * Token response from Keycloak.
     */
    public static class TokenResponse {
        private String access_token;
        private int expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public void setAccessToken(String accessToken) {
            this.access_token = accessToken;
        }

        public int getExpiresIn() {
            return expires_in;
        }

        public void setExpiresIn(int expiresIn) {
            this.expires_in = expiresIn;
        }
    }
}
