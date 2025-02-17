package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.web.client.RestTemplate;

public interface OAuthRestTemplate {

    RestTemplate oauthCustomClientTokenRestTemplate();

    RestTemplate oauthRequestTokenRestTemplate();
}
