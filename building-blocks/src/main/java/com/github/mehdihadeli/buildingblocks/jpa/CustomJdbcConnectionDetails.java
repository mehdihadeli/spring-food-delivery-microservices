package com.github.mehdihadeli.buildingblocks.jpa;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;

public class CustomJdbcConnectionDetails implements JdbcConnectionDetails {
    private final DataSourceProperties dataSourceProperties;

    public CustomJdbcConnectionDetails(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public String getUsername() {
        return dataSourceProperties.determineUsername();
    }

    @Override
    public String getPassword() {
        return dataSourceProperties.determinePassword();
    }

    @Override
    public String getJdbcUrl() {
        return dataSourceProperties.determineUrl();
    }

    @Override
    public String getDriverClassName() {
        return dataSourceProperties.determineDriverClassName();
    }
}
