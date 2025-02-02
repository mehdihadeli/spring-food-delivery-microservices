package com.github.mehdihadeli.buildingblocks.test.fixtures;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainerFixture {
    private final PostgreSQLContainer<?> postgresContainer;

    public PostgresTestContainerFixture() {
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

    public void resetDatabase() {
        // TODO
    }

    public void stopContainer() {
        postgresContainer.stop();
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
