package com.github.mehdihadeli.buildingblocks.security;

import com.github.mehdihadeli.buildingblocks.security.tokenaccessors.OAuthCustomClientTokenAccessor;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ClientOAuthTokenInterceptor implements ClientHttpRequestInterceptor {

    private final OAuthCustomClientTokenAccessor clientTokenAccessor;

    public ClientOAuthTokenInterceptor(OAuthCustomClientTokenAccessor tokenAccessor) {
        this.clientTokenAccessor = tokenAccessor;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        // Get or generate the token
        String accessToken = clientTokenAccessor.getClientToken().accessToken();

        // Add the token to the request headers
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        // Execute the request
        return execution.execute(request, body);
    }
}
