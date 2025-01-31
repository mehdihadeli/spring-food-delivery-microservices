package com.github.mehdihadeli.buildingblocks.swagger;

import com.github.mehdihadeli.buildingblocks.core.utils.PropertiesUtils;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

@Order(Ordered.LOWEST_PRECEDENCE)
public class SwaggerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // to enable or disable springdoc package `@ConditionalOnProperty(name = {"springdoc.swagger-ui.enabled"})` that
        // should happen before discovering `auto-configurations` otherwise ConditionalOnProperty always evaluate to its
        // default which is true
        PropertiesUtils.updateProperty(
                environment,
                "springdoc.api-docs.enabled",
                environment.getProperty("swagger.enabled", Boolean.class, false));
        PropertiesUtils.updateProperty(
                environment,
                "springdoc.swagger-ui.enabled",
                environment.getProperty("swagger.enabled", Boolean.class, false));
    }
}
