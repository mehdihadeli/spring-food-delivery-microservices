package com.github.mehdihadeli.buildingblocks.core;

import com.github.mehdihadeli.buildingblocks.core.utils.PropertiesUtils;
import org.springframework.core.env.ConfigurableEnvironment;

// https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html
// https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/env/ConfigurableEnvironment.html

public class PropertiesService {
    private final ConfigurableEnvironment environment;

    PropertiesService(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    // for applying update on properties we can't update strongly type bind properties of @Value, we should add or
    // update `PropertySource` for adding or updating existing properties
    public void updateProperty(String key, Object value) {
        PropertiesUtils.updateProperty(getEnvironment(), key, value);
    }

    public void updateProperty(String propertySourceName, String key, Object value) {
        PropertiesUtils.updateProperty(getEnvironment(), propertySourceName, key, value);
    }
}
