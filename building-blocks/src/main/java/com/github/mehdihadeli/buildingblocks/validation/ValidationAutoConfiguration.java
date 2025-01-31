package com.github.mehdihadeli.buildingblocks.validation;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = CoreAutoConfiguration.class)
@EnableConfigurationProperties(ValidationProperties.class)
@ConditionalOnProperty(prefix = "validation", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication
@Import(ValidationConfiguration.class)
public class ValidationAutoConfiguration {
    ValidationAutoConfiguration() {}
}
