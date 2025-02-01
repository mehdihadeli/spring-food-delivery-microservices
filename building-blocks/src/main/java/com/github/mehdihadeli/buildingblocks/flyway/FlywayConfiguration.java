package com.github.mehdihadeli.buildingblocks.flyway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// https://docs.spring.io/spring-boot/how-to/data-initialization.html#howto.data-initialization.migration-tool
// https://blog.jetbrains.com/idea/2024/11/how-to-use-flyway-for-database-migrations-in-spring-boot-applications/

// By default, Flyway autowires the (@Primary) DataSource in our context
@Configuration(proxyBeanMethods = false)
public class FlywayConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfiguration.class);

    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer(FlywayProperties flywayProperties) {
        return configuration -> {
            // Check and set properties if not configured in FlywayProperties
            if (flywayProperties.getBaselineVersion() == null) {
                configuration.baselineVersion("1.0.0"); // Set default baseline version
            }

            if (flywayProperties.getLocations().isEmpty()) {
                configuration.locations("classpath:db/migration"); // Set default locations
            }

            if (flywayProperties.getSqlMigrationPrefix() == null) {
                configuration.sqlMigrationPrefix("V"); // Set default migration prefix
            }

            // Additional configurations
            configuration.placeholderReplacement(false); // Disable placeholder replacement
            configuration.validateOnMigrate(true); // Enable validation before migration
        };
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(FlywayProperties flywayProperties) {
        return flyway -> {
            try {
                // Custom migration logic
                logger.info("Applying Flyway migrations...");

                // Example: Run only if a specific condition is met
                if (flywayProperties.isEnabled()) {
                    flyway.migrate();
                    logger.info("Migrations applied successfully.");
                } else {
                    logger.info("Skipping migrations.");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        };
    }
}
