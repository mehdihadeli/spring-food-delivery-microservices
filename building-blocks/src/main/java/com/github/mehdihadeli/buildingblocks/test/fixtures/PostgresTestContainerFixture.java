package com.github.mehdihadeli.buildingblocks.test.fixtures;

import com.github.mehdihadeli.buildingblocks.jpa.CustomJdbcConnectionDetails;
import java.util.List;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainerFixture {
    private static final Logger logger = LoggerFactory.getLogger(PostgresTestContainerFixture.class);
    private final PostgreSQLContainer<?> postgresContainer;

    // similar to DbConnection in ADO.Net and dapper in .net as lower level database operations abstraction and
    // EntityManager like DBContext in .net as higher level orm abstraction
    private JdbcTemplate jdbcTemplate;

    public PostgresTestContainerFixture() {
        this.postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("test_db")
                .withUsername("test")
                .withPassword("test");
    }

    public void startContainer() {
        if (!postgresContainer.isRunning()) {
            postgresContainer.start();
            // should be after container starting
            jdbcTemplate = new JdbcTemplate(createDataSource());
        }
    }

    public void stopContainer() {
        postgresContainer.stop();
    }

    /**
     * Truncates all tables in the database with CASCADE.
     */
    public void resetDatabase() {
        ensureDatabaseExists(jdbcTemplate);

        List<String> tableNames = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class);

        // Step 2: Construct the TRUNCATE CASCADE command
        if (!tableNames.isEmpty()) {
            String truncateSql = "TRUNCATE TABLE " + String.join(", ", tableNames) + " CASCADE";
            jdbcTemplate.execute(truncateSql);
            logger.info("Truncated all tables: {}", tableNames);
        } else {
            logger.info("No tables found to truncate.");
        }
    }

    private void ensureDatabaseExists(JdbcTemplate jdbcTemplate) {
        try {

            var databaseName = postgresContainer.getDatabaseName();
            // Check if the database exists
            String checkDbSql = "SELECT 1 FROM pg_database WHERE datname = ?";
            List<Integer> result = jdbcTemplate.queryForList(checkDbSql, Integer.class, databaseName);

            // Create the database if it doesn't exist
            if (result.isEmpty()) {
                String createDbSql = "CREATE DATABASE " + postgresContainer;
                jdbcTemplate.execute(createDbSql);
                System.out.println("Database created: " + postgresContainer);
            } else {
                System.out.println("Database already exists: " + postgresContainer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    public String getUsername() {
        return postgresContainer.getUsername();
    }

    public String getPassword() {
        return postgresContainer.getPassword();
    }

    public String getDriverClassName() {
        return postgresContainer.getDriverClassName();
    }

    public String getDatabaseName() {
        return postgresContainer.getDatabaseName();
    }

    /**
     * Creates a DataSource using the PostgreSQL container's connection details.
     */
    private DataSource createDataSource() {
        var dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setPassword(postgresContainer.getPassword());
        dataSourceProperties.setUsername(postgresContainer.getUsername());
        dataSourceProperties.setUrl(postgresContainer.getJdbcUrl());

        var jdbcConnectionDetails = new CustomJdbcConnectionDetails(dataSourceProperties);
        var dataSource = DataSourceBuilder.create()
                .type(PGSimpleDataSource.class) // Use PGSimpleDataSource instead of Hikari
                .driverClassName(jdbcConnectionDetails.getDriverClassName())
                .url(jdbcConnectionDetails.getJdbcUrl())
                .username(jdbcConnectionDetails.getUsername())
                .password(jdbcConnectionDetails.getPassword())
                .build();

        return dataSource;
    }
}
