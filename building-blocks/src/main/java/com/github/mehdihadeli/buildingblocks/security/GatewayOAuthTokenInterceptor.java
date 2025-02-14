package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.io.IOException;

public class GatewayOAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private final RequestTokenAccessor requestTokenAccessor;

    public GatewayOAuthTokenInterceptor(RequestTokenAccessor requestTokenAccessor) {
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
