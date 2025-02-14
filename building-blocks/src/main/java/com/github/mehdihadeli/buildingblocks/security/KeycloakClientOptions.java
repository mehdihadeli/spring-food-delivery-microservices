package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "spring.security.oauth2.keycloak-client-options")
public class KeycloakClientOptions {

    private String baseUrl;
    private String tokenUri;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String authUri; // For Authorization Code Flow
    private String redirectUri; // For Authorization Code Flow
    private String userInfoUri; // For fetching user info

    private String scope;
    private Map<String, String> additionalConfigs = new HashMap<>();

    // Getters and setters

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthUri() {
        return authUri;
    }

    public void setAuthUri(String authUri) {
        this.authUri = authUri;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, String> getAdditionalConfigs() {
        return additionalConfigs;
    }

    public void setAdditionalConfigs(Map<String, String> additionalConfigs) {
        this.additionalConfigs = additionalConfigs;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
