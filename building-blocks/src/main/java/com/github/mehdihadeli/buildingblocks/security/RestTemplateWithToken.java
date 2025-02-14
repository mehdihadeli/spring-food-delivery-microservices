package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.web.client.RestTemplate;

public interface RestTemplateWithToken {
    RestTemplate getClientOauthRestTemplate();

    RestTemplate getOauthRestTemplate();
}
