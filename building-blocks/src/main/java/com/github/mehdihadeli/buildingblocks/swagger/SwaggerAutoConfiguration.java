package com.github.mehdihadeli.buildingblocks.swagger;

import com.github.mehdihadeli.buildingblocks.core.CoreAutoConfiguration;
import com.github.mehdihadeli.buildingblocks.core.PropertiesService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@AutoConfiguration(
        before = {WebMvcAutoConfiguration.class},
        after = {CoreAutoConfiguration.class})
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnBean({PropertiesService.class})
// This condition ensures that the configuration is only applied if a specific property is set in the application’s
// configuration
// @ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SwaggerProperties.class)
@Import(SwaggerConfiguration.class)
public class SwaggerAutoConfiguration {}
