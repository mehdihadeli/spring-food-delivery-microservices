package com.github.mehdihadeli.buildingblocks.security.tokenaccessors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

public interface OAuthRequestTokenAccessor {
    OAuth2AccessToken getRequestAccessTokenForCurrentUser();

    OAuth2AccessToken getAccessToken(Authentication auth);

    OAuth2RefreshToken getRefreshToken(Authentication auth);
}
