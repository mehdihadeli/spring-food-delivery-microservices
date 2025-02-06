package com.github.mehdihadeli.buildingblocks.test.fixtures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public class PostgresTestContainerFixture {
    private static final Logger logger = LoggerFactory.getLogger(PostgresTestContainerFixture.class);

    private final PostgreSQLContainer<?> postgresContainer;
    // similar to DbConnection in ADO.Net and dapper in .net as lower level database operations abstraction and
    // EntityManager like DBContext in .net as higher level orm abstraction
    private final JdbcTemplate jdbcTemplate;

    public PostgresTestContainerFixture(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        //// or we can create a new JdbcTemplate based on `dataSource`
        // this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("test_db")
                .withUsername("test")
                .withPassword("test");
    }

    public void startContainer() {
        if (!postgresContainer.isRunning()) {
            postgresContainer.start();
        }
    }

    public void stopContainer() {
        postgresContainer.stop();
    }

    /**
     * Truncates all tables in the database with CASCADE.
     */
    public void resetDatabase() {
        // Step 0: ensure database exists
        ensureDatabaseExists();
        // Step 1: Get all table names
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

    private void ensureDatabaseExists() {
        // Check if the database exists
        String checkDbSql = "SELECT 1 FROM pg_database WHERE datname = ?";
        List<Integer> result =
                jdbcTemplate.queryForList(checkDbSql, Integer.class, postgresContainer.getDatabaseName());

        // Create the database if it doesn't exist
        if (result.isEmpty()) {
            String createDbSql = "CREATE DATABASE " + postgresContainer;
            jdbcTemplate.execute(createDbSql);
            System.out.println("Database created: " + postgresContainer);
        } else {
            System.out.println("Database already exists: " + postgresContainer);
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

    public String getDatabaseName() {
        return postgresContainer.getDatabaseName();
    }
}
