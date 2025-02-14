package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Duration;
import java.time.Instant;

// ref:
// https://github.com/thomasdarimont/keycloak-project-example/blob/main/apps/bff-springboot3/src/main/java/com/github/thomasdarimont/apps/bff3/oauth/TokenAccessor.java

/**
 * Provides access to OAuth2 access- and refresh-tokens of an authenticated user.
 */
public class RequestTokenAccessor {

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final TokenRefresher tokenRefresher;

    public RequestTokenAccessor(OAuth2AuthorizedClientService authorizedClientService, TokenRefresher tokenRefresher) {
        this.authorizedClientService = authorizedClientService;
        this.tokenRefresher = tokenRefresher;
    }

    private final Duration accessTokenExpiresSkew = Duration.ofSeconds(10);

    public OAuth2AccessToken getRequestAccessTokenForCurrentUser() {
        return getAccessToken(SecurityContextHolder.getContext().getAuthentication());
    }

    public OAuth2AccessToken getAccessToken(Authentication auth) {

        var client = getOAuth2AuthorizedClient(auth);
        if (client == null) {
            return null;
        }

        var accessToken = client.getAccessToken();
        if (accessToken == null) {
            return null;
        }

        var accessTokenStillValid = isAccessTokenStillValid(accessToken);
        boolean tokenRefreshEnabled = true;
        if (!accessTokenStillValid && tokenRefreshEnabled) {
            accessToken = tokenRefresher.refreshTokens(client);
        }

        return accessToken;
    }

    public OAuth2RefreshToken getRefreshToken(Authentication auth) {

        OAuth2AuthorizedClient client = getOAuth2AuthorizedClient(auth);
        if (client == null) {
            return null;
        }
        return client.getRefreshToken();
    }

    private boolean isAccessTokenStillValid(OAuth2AccessToken accessToken) {
        var expiresAt = accessToken.getExpiresAt();
        if (expiresAt == null) {
            return false;
        }
        var exp = expiresAt.minus(accessTokenExpiresSkew == null ? Duration.ofSeconds(0) : accessTokenExpiresSkew);
        var now = Instant.now();

        return now.isBefore(exp);
    }

    private OAuth2AuthorizedClient getOAuth2AuthorizedClient(Authentication auth) {

        var authToken = (OAuth2AuthenticationToken) auth;
        var clientId = authToken.getAuthorizedClientRegistrationId();
        var username = auth.getName();
        return authorizedClientService.loadAuthorizedClient(clientId, username);
    }
}
