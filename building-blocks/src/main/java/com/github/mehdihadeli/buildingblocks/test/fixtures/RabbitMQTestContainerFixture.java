package com.github.mehdihadeli.buildingblocks.test.fixtures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public class RabbitMQTestContainerFixture {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQTestContainerFixture.class);
    private final RabbitMQContainer rabbitMQContainer;
    private final TestRestTemplate restTemplate;
    private RabbitAdmin rabbitAdmin;

    public RabbitMQTestContainerFixture() {
        this.restTemplate = new TestRestTemplate();
        this.rabbitMQContainer = new RabbitMQContainer("rabbitmq:management");
    }

    public void startContainer() {
        if (!rabbitMQContainer.isRunning()) {
            rabbitMQContainer.start();
            rabbitAdmin = new RabbitAdmin(connectionFactory());
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
        String queueUrl = format("http://%s:%d/api/queues", host, apiPort);
        ResponseEntity<List<QueueResponse>> response = restTemplate.exchange(
                queueUrl,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(getUserName(), getPassword())),
                new ParameterizedTypeReference<List<QueueResponse>>() {});

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<QueueResponse> queues = response.getBody();
            for (QueueResponse queue : queues) {
                logger.info("Purging messages from queue: {}", queue);
                rabbitAdmin.deleteQueue(queue.getName());
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
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return headers;
    }

    static class QueueResponse {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    CachingConnectionFactory connectionFactory() {
        // https://docs.spring.io/spring-amqp/reference/amqp/connections.html#choosing-factory
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitMQContainer.getHost());
        connectionFactory.setPort(rabbitMQContainer.getAmqpPort());
        connectionFactory.setUsername(rabbitMQContainer.getAdminUsername());
        connectionFactory.setPassword(rabbitMQContainer.getAdminPassword());
        connectionFactory.setVirtualHost("/");

        return connectionFactory;
    }
}
