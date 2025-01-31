package com.github.mehdihadeli.buildingblocks.mediator;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

// https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html
// https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html#features.developing-auto-configuration.condition-annotations

@AutoConfiguration
// The condition checks if both these classes are available in the application’s classpath at runtime. If either of the
// specified classes is not available, the configuration will not be applied.
@ConditionalOnClass({Mediator.class})
@EnableConfigurationProperties(MediatorProperties.class)
// This condition ensures that the configuration is only applied if a specific property is set in the application’s
// configuration
@ConditionalOnProperty(prefix = "mediator", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(MediatorConfiguration.class)
public class MediatorAutoConfiguration {}
