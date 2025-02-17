package com.github.mehdihadeli.buildingblocks.security;

import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthRequestTokenAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.io.IOException;

public class OAuthRequestTokenInterceptor implements ClientHttpRequestInterceptor {

    private final OAuthRequestTokenAccessor requestTokenAccessor;

    public OAuthRequestTokenInterceptor(OAuthRequestTokenAccessor requestTokenAccessor) {
        this.requestTokenAccessor = requestTokenAccessor;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        // Get or generate the token
        var accessToken = requestTokenAccessor.getRequestAccessTokenForCurrentUser();
        if (accessToken == null) {
            throw new OAuth2AuthenticationException("missing access token");
        }

        // Add the token to the request headers
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Execute the request
        return execution.execute(request, body);
    }
}
