package com.github.mehdihadeli.buildingblocks.validation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// when `proxyBeanMethods = false`, avoids the direct method call problem that would occur when one @Bean method calls
// another internally.
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(ValidationProperties.class)
@ConditionalOnClass({Validator.class})
@ConditionalOnProperty(prefix = "validation", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ValidationConfiguration {
    ValidationConfiguration() {}
}
