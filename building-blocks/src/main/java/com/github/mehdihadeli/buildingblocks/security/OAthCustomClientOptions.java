package com.github.mehdihadeli.buildingblocks.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.custom-client-options")
public class OAthCustomClientOptions {

    private String hostUrl;
    private String clientId;
    private String adminRealmName;
    private String applicationRealmName;
    private String clientSecret;
    private String userName;
    private String password;
    private String grantType;
    private String redirectUri; // For Authorization Code Flow
    private String userInfoUri; // For fetching user info

    private String scope;
    private Map<String, String> additionalConfigs = new HashMap<>();

    // Getters and setters

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

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getAdminRealmName() {
        return adminRealmName;
    }

    public void setAdminRealmName(String adminRealmName) {
        this.adminRealmName = adminRealmName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApplicationRealmName() {
        return applicationRealmName;
    }

    public void setApplicationRealmName(String applicationRealmName) {
        this.applicationRealmName = applicationRealmName;
    }
}
