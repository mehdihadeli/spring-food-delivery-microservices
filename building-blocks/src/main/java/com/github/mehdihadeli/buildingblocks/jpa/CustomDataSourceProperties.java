package com.github.mehdihadeli.buildingblocks.jpa;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
public class CustomDataSourceProperties {
    private boolean useInMemory = false;

    public boolean isUseInMemory() {
        return useInMemory;
    }

    public void setUseInMemory(boolean useInMemory) {
        this.useInMemory = useInMemory;
    }
}
