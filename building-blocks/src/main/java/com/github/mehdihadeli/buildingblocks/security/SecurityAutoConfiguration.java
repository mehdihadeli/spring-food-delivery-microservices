package com.github.mehdihadeli.buildingblocks.security;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(KeycloakClientOptions.class)
@Import(SecurityConfiguration.class)
public class SecurityAutoConfiguration {}
