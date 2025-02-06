package com.github.mehdihadeli.buildingblocks.test.fixtures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.Base64;

public class RabbitMQTestContainerFixture {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQTestContainerFixture.class);
    private final RabbitMQContainer rabbitMQContainer;
    private final RabbitAdmin rabbitAdmin;
    private final RestTemplate restTemplate;

    public RabbitMQTestContainerFixture(RabbitAdmin rabbitAdmin, RestTemplate restTemplate) {
        this.rabbitAdmin = rabbitAdmin;
        this.restTemplate = restTemplate;
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

    /**
     * Cleans up all queues by purging messages or deleting queues.
     */
    public void cleanupQueues() {
        int apiPort = rabbitMQContainer.getHttpPort();
        String host = rabbitMQContainer.getHost();

        // Get all queues
        String queueUrl = String.format("http://%s:%d/api/queues", host, apiPort);
        ResponseEntity<QueueInformation[]> response = restTemplate.exchange(
                queueUrl,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(getUserName(), getPassword())),
                QueueInformation[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            QueueInformation[] queues = response.getBody();
            for (QueueInformation queue : queues) {
                logger.info("Purging messages from queue: {}", queue);
                rabbitAdmin.purgeQueue(queue.getName(), true); // Purge messages from the queue
            }
        } else {
            logger.warn("Failed to retrieve queues. Status: {}", response.getStatusCode());
        }
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

    /**
     * Creates HTTP headers with basic authentication.
     *
     * @param username The username.
     * @param password The password.
     * @return The HTTP headers.
     */
    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        return headers;
    }
}
