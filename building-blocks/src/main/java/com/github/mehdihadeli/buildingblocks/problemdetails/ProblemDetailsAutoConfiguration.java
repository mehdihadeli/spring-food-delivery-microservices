package com.github.mehdihadeli.buildingblocks.problemdetails;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = CoreAutoConfiguration.class)
@EnableConfigurationProperties(ProblemDetailsProperties.class)
@ConditionalOnProperty(prefix = "problem-details", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication
@Import(ProblemDetailsConfiguration.class)
public class ProblemDetailsAutoConfiguration {}
