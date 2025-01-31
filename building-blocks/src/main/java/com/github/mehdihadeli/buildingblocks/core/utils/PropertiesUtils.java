package com.github.mehdihadeli.buildingblocks.core.utils;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

public final class PropertiesUtils {

    private PropertiesUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static final String DYNAMIC_PROPERTIES_SOURCE_NAME = "dynamicProperties";

    // for applying update on properties we can't update strongly type bind properties of @Value, we should add or
    // update `PropertySource` for adding or updating existing properties
    public static void updateProperty(ConfigurableEnvironment configurableEnvironment, String key, Object value) {
        updateProperty(configurableEnvironment, DYNAMIC_PROPERTIES_SOURCE_NAME, key, value);
    }

    public static void updateProperty(
            ConfigurableEnvironment configurableEnvironment, String propertySourceName, String key, Object value) {
        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
        if (!mutablePropertySources.contains(propertySourceName)) {
            Map<String, Object> dynamicProperties = new HashMap<>();
            dynamicProperties.put(key, value);
            // We added the new property source as the first item because we want it to override any existing property
            // with the same key.
            mutablePropertySources.addFirst(new MapPropertySource(propertySourceName, dynamicProperties));
        } else {
            MapPropertySource propertySource = (MapPropertySource) mutablePropertySources.get(propertySourceName);
            propertySource.getSource().put(key, value);
        }
    }
}
