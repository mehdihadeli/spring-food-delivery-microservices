package com.github.mehdihadeli.buildingblocks.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

// ref:
// https://github.com/thomasdarimont/keycloak-project-example/blob/0d9745a4bfa520af4316fc71ca332b31bdc2440c/apps/bff-springboot3/src/main/java/com/github/thomasdarimont/apps/bff3/support/HttpSessionOAuth2AuthorizedClientService.java

public class HttpSessionOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(
            String clientRegistrationId, String principalName) {

        return (T) HttpServletRequestUtils.getCurrentHttpSession(false)
                .map(sess -> sess.getAttribute(clientRegistrationId))
                .orElse(null);
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {

        HttpServletRequestUtils.getCurrentHttpSession(false)
                .ifPresent(sess -> sess.setAttribute(
                        authorizedClient.getClientRegistration().getRegistrationId(), authorizedClient));
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

        HttpServletRequestUtils.getCurrentHttpSession(false)
                .ifPresent(sess -> sess.removeAttribute(clientRegistrationId));
    }
}

class HttpServletRequestUtils {

    public static Optional<HttpServletRequest> getCurrentHttpServletRequest() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Optional.ofNullable(servletRequestAttributes).map(ServletRequestAttributes::getRequest);
    }

    public static Optional<HttpSession> getCurrentHttpSession(boolean create) {
        return getCurrentHttpServletRequest().map(req -> req.getSession(false));
    }
}
