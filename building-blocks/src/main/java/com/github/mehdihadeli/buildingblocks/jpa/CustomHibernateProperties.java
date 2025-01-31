package com.github.mehdihadeli.buildingblocks.jpa;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@ConfigurationProperties("spring.jpa.hibernate")
public class CustomHibernateProperties {

    private boolean formatSql = true;
    private int batchSize = 50;
    private int fetchSize = 100;
    private String timeZone = "UTC";
    private boolean orderInserts = true;
    private boolean orderUpdates = true;
    private long queryTimeout = 3000;
    private boolean hibernateUseSqlComments = true;
    private boolean generateStatistics = true;
    private String connectionReleaseMode = "after_transaction";

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isOrderInserts() {
        return orderInserts;
    }

    public void setOrderInserts(boolean orderInserts) {
        this.orderInserts = orderInserts;
    }

    public boolean isOrderUpdates() {
        return orderUpdates;
    }

    public void setOrderUpdates(boolean orderUpdates) {
        this.orderUpdates = orderUpdates;
    }

    public long getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(long queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public boolean isHibernateUseSqlComments() {
        return hibernateUseSqlComments;
    }

    public void setHibernateUseSqlComments(boolean hibernateUseSqlComments) {
        this.hibernateUseSqlComments = hibernateUseSqlComments;
    }

    public boolean isGenerateStatistics() {
        return generateStatistics;
    }

    public void setGenerateStatistics(boolean generateStatistics) {
        this.generateStatistics = generateStatistics;
    }

    public String getConnectionReleaseMode() {
        return connectionReleaseMode;
    }

    public void setConnectionReleaseMode(String connectionReleaseMode) {
        this.connectionReleaseMode = connectionReleaseMode;
    }

    public boolean isFormatSql() {
        return formatSql;
    }

    public void setFormatSql(boolean formatSql) {
        this.formatSql = formatSql;
    }

    public Map<String, Object> determineHibernateProperties(
            Map<String, String> existingProperties, HibernateProperties hibernateProperties) {
        Map<String, Object> properties = new HashMap<>();

        // Apply naming strategies
        applyNamingStrategies(properties, hibernateProperties);

        // Handle DDL auto configuration
        String determinedDdlAuto = determineDdlAuto(existingProperties, hibernateProperties);
        if (determinedDdlAuto != null) {
            properties.put(AvailableSettings.HBM2DDL_AUTO, determinedDdlAuto);
        }
        // Common settings
        if (existingProperties.get(AvailableSettings.STATEMENT_BATCH_SIZE) == null) {
            properties.put(AvailableSettings.STATEMENT_BATCH_SIZE, getBatchSize());
        }
        if (existingProperties.get(AvailableSettings.STATEMENT_FETCH_SIZE) == null) {
            properties.put(AvailableSettings.STATEMENT_FETCH_SIZE, getFetchSize());
        }
        if (existingProperties.get(AvailableSettings.JDBC_TIME_ZONE) == null) {
            properties.put(AvailableSettings.JDBC_TIME_ZONE, getTimeZone());
        }
        if (existingProperties.get(AvailableSettings.FORMAT_SQL) == null) {
            properties.put(AvailableSettings.FORMAT_SQL, isFormatSql());
        }
        if (existingProperties.get(AvailableSettings.ORDER_INSERTS) == null) {
            properties.put(AvailableSettings.ORDER_INSERTS, isOrderInserts());
        }
        if (existingProperties.get(AvailableSettings.ORDER_INSERTS) == null) {
            properties.put(AvailableSettings.ORDER_INSERTS, isOrderUpdates());
        }
        if (existingProperties.get(AvailableSettings.USE_SQL_COMMENTS) == null) {
            properties.put(AvailableSettings.USE_SQL_COMMENTS, isHibernateUseSqlComments());
        }
        if (existingProperties.get(AvailableSettings.GENERATE_STATISTICS) == null) {
            properties.put(AvailableSettings.GENERATE_STATISTICS, isGenerateStatistics());
        }
        if (existingProperties.get("hibernate.connection.release_mode") == null) {
            properties.put("hibernate.connection.release_mode", getConnectionReleaseMode());
        }
        if (existingProperties.get("hibernate.query.timeout") == null) {
            properties.put("hibernate.query.timeout", getQueryTimeout());
        }

        return properties;
    }

    private String determineDdlAuto(Map<String, String> existing, HibernateProperties hibernateProperties) {
        String existingDdlAuto = existing.get(AvailableSettings.HBM2DDL_AUTO);
        if (existingDdlAuto != null) {
            return existingDdlAuto;
        }

        if (hibernateProperties.getDdlAuto() != null) {
            return hibernateProperties.getDdlAuto();
        }

        String schemaGeneration = existing.get(AvailableSettings.JAKARTA_HBM2DDL_DATABASE_ACTION);
        if (schemaGeneration != null) {
            return null;
        }

        return "none"; // default DDL mode
    }

    private void applyNamingStrategies(Map<String, Object> properties, HibernateProperties hibernateProperties) {
        this.applyNamingStrategy(
                properties,
                "hibernate.implicit_naming_strategy",
                hibernateProperties.getNaming().getImplicitStrategy(),
                SpringImplicitNamingStrategy.class::getName);
        this.applyNamingStrategy(
                properties,
                "hibernate.physical_naming_strategy",
                hibernateProperties.getNaming().getPhysicalStrategy(),
                CamelCaseToUnderscoresNamingStrategy.class::getName);
    }

    private void applyNamingStrategy(
            Map<String, Object> properties, String key, Object strategy, Supplier<String> defaultStrategy) {
        if (strategy != null) {
            properties.put(key, strategy);
        } else {
            properties.computeIfAbsent(key, (k) -> defaultStrategy.get());
        }
    }
}
