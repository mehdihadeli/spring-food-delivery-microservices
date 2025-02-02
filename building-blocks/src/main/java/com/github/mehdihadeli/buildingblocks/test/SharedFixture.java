package com.github.mehdihadeli.buildingblocks.test;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.test.fixtures.PostgresTestContainerFixture;
import com.github.mehdihadeli.buildingblocks.test.fixtures.RabbitMQTestContainerFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.junit.jupiter.Container;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SharedFixture {
    private final ApplicationContext applicationContext;
    private final TestRestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final Mediator mediator;

    public SharedFixture(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.restTemplate = applicationContext.getBean(TestRestTemplate.class);
        this.rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        this.mediator = applicationContext.getBean(Mediator.class);
    }

    @Container
    private static final PostgresTestContainerFixture postgresContainer = new PostgresTestContainerFixture();

    private static final RabbitMQTestContainerFixture rabbitmqContainer = new RabbitMQTestContainerFixture();
    protected static WireMockServer wireMockServer;

    public void initialize() {
        postgresContainer.startContainer();
        rabbitmqContainer.startContainer();
        // Initialize WireMock server
        // Start WireMock server on a specific port
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    public void dispose() {
        postgresContainer.stopContainer();
        rabbitmqContainer.stopContainer();
    }

    public void configureProperties(DynamicPropertyRegistry registry) {
        configurePostgresProperties(registry);
        configureRabbitMQProperties(registry);
        wireMockServer.stop();
    }

    public <TResponse> TResponse sendAsync(IRequest<TResponse> request) {
        return mediator.send(request);
    }

    public void resetDatabasesAsync() {
        postgresContainer.resetDatabase();
    }

    private void configurePostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    private void configureRabbitMQProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitmqContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitmqContainer::getPort);
        registry.add("spring.rabbitmq.username", rabbitmqContainer::getUserName);
        registry.add("spring.rabbitmq.password", rabbitmqContainer::getPassword);
        // Optional: Configure virtual host if needed
        registry.add("spring.rabbitmq.virtual-host", () -> "/");
    }

    public void waitUntilConditionMet(
            Callable<Boolean> conditionToMet, Integer timeoutSeconds, String exceptionMessage) {
        int timeout = timeoutSeconds != null ? timeoutSeconds : 300; // Default to 300 seconds
        long startTime = System.currentTimeMillis();
        boolean conditionMet;
        try {
            conditionMet = conditionToMet.call();

            while (!conditionMet) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                if (elapsedTime > timeout) {
                    try {
                        throw new TimeoutException(
                                exceptionMessage != null
                                        ? exceptionMessage
                                        : "Condition not met within the timeout period.");
                    } catch (TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                }

                TimeUnit.MILLISECONDS.sleep(100);
                conditionMet = conditionToMet.call();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
