package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.web.client.RestTemplate;

public class RestTemplateWithTokenImpl implements RestTemplateWithToken {
    private final RestTemplate clientOauthRestTemplate;
    private final RestTemplate oauthRestTemplate;

    public RestTemplateWithTokenImpl(RestTemplate clientOauthRestTemplate, RestTemplate oauthRestTemplate) {
        this.clientOauthRestTemplate = clientOauthRestTemplate;
        this.oauthRestTemplate = oauthRestTemplate;
    }

    @Override
    public RestTemplate getClientOauthRestTemplate() {
        return clientOauthRestTemplate;
    }

    @Override
    public RestTemplate getOauthRestTemplate() {
        return oauthRestTemplate;
    }
}
