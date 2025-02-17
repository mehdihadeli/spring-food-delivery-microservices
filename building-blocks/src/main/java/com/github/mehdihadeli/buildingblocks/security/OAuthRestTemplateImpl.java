package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.web.client.RestTemplate;

public class OAuthRestTemplateImpl implements OAuthRestTemplate {

    private final RestTemplate oauthCustomClientTokenRestTemplate;
    private final RestTemplate oauthRequestTokenRestTemplate;

    public OAuthRestTemplateImpl(
            RestTemplate oauthCustomClientTokenRestTemplate, RestTemplate oauthRequestTokenRestTemplate) {
        this.oauthCustomClientTokenRestTemplate = oauthCustomClientTokenRestTemplate;
        this.oauthRequestTokenRestTemplate = oauthRequestTokenRestTemplate;
    }

    public RestTemplate oauthCustomClientTokenRestTemplate() {
        return oauthCustomClientTokenRestTemplate;
    }

    public RestTemplate oauthRequestTokenRestTemplate() {
        return oauthRequestTokenRestTemplate;
    }
}
