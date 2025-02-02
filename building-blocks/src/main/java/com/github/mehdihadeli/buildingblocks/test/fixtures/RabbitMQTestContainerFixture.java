package com.github.mehdihadeli.buildingblocks.test.fixtures;

import org.testcontainers.containers.RabbitMQContainer;

public class RabbitMQTestContainerFixture {
    private final RabbitMQContainer rabbitMQContainer;

    public RabbitMQTestContainerFixture() {
        this.rabbitMQContainer = new RabbitMQContainer("rabbitmq:latest").withUser("guest", "guest");
    }

    public void startContainer() {
        if (!rabbitMQContainer.isRunning()) {
            rabbitMQContainer.start();
        }
    }

    public void stopContainer() {
        rabbitMQContainer.stop();
    }

    public String getAmqpUrl() {
        return rabbitMQContainer.getAmqpUrl();
    }

    public String getHost() {
        return rabbitMQContainer.getHost();
    }

    public Integer getPort() {
        return rabbitMQContainer.getAmqpPort();
    }

    public String getUserName() {
        return rabbitMQContainer.getAdminUsername();
    }

    public String getPassword() {
        return rabbitMQContainer.getAdminPassword();
    }
}
