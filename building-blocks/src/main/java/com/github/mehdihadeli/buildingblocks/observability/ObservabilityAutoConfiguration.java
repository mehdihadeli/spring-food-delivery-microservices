package com.github.mehdihadeli.buildingblocks.observability;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(ObservabilityProperties.class)
@Import(ObservabilityConfiguration.class)
public class ObservabilityAutoConfiguration {}
