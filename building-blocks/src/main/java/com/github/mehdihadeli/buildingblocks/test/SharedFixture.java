package com.github.mehdihadeli.buildingblocks.test;

import com.github.mehdihadeli.buildingblocks.abstractions.core.bean.BeanScopeExecutor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommand;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.commands.ICommandUnit;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessage;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelope;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.queries.IQuery;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.requests.IRequest;
import com.github.mehdihadeli.buildingblocks.test.fixtures.MongoTestContainerFixture;
import com.github.mehdihadeli.buildingblocks.test.fixtures.PostgresTestContainerFixture;
import com.github.mehdihadeli.buildingblocks.test.fixtures.RabbitMQTestContainerFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public class SharedFixture {
    private final ApplicationContext applicationContext;
    private final TestRestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final BeanScopeExecutor beanScopeExecutor;
    private final TestRabbitTemplate testRabbitTemplate;
    private final String apiUrl;

    private final PostgresTestContainerFixture postgresContainer;
    private final RabbitMQTestContainerFixture rabbitmqContainer;
    private final MongoTestContainerFixture mongoTestContainer;

    public SharedFixture(ApplicationContext applicationContext, String apiUrl) {
        this.applicationContext = applicationContext;
        this.postgresContainer = new PostgresTestContainerFixture(applicationContext.getBean(JdbcTemplate.class));
        this.rabbitmqContainer = new RabbitMQTestContainerFixture(
                applicationContext.getBean(RabbitAdmin.class), applicationContext.getBean(RestTemplate.class));
        this.mongoTestContainer = new MongoTestContainerFixture();
        this.restTemplate = applicationContext.getBean(TestRestTemplate.class);
        this.rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        this.beanScopeExecutor = applicationContext.getBean(BeanScopeExecutor.class);
        this.apiUrl = apiUrl;
        // Use TestRabbitTemplate to intercept and verify the message
        this.testRabbitTemplate = new TestRabbitTemplate(rabbitTemplate.getConnectionFactory());
    }

    protected static WireMockServer wireMockServer;

    public void initialize() {
        postgresContainer.startContainer();
        rabbitmqContainer.startContainer();
        mongoTestContainer.startContainer();
        // Initialize WireMock server
        // Start WireMock server on a specific port
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    public void dispose() {
        postgresContainer.stopContainer();
        rabbitmqContainer.stopContainer();
        mongoTestContainer.stopContainer();
        wireMockServer.stop();
    }

    @DynamicPropertySource
    public void configureTestProperties(DynamicPropertyRegistry registry) {
        configurePostgresProperties(registry);
        configureMongoProperties(registry);
        configureRabbitMQProperties(registry);
    }

    public void resetDatabasesAsync() {
        postgresContainer.resetDatabase();
        mongoTestContainer.resetDatabase();
    }

    public void cleanupMessaging() {
        rabbitmqContainer.cleanupQueues();
    }

    private void configurePostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    private void configureMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoTestContainer::getConnectionString);
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

    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and performs an operation.
     */
    public void executeInScope(Consumer<ApplicationContext> action) {
        beanScopeExecutor.executeInScope(action);
    }

    /**
     * Executes the provided action within a scoped context.
     *
     * @param action The action to execute, which accepts a service provider and returns a result.
     * @param <T>    The type of the result.
     * @return The result of the action.
     */
    public <T> T executeInScope(Function<ApplicationContext, T> action) {
      return beanScopeExecutor.executeInScope(action);
    }

    /**
     * Executes a request using the Mediator pattern.
     *
     * @param request The request to execute.
     * @param <TResponse> The type of the response.
     * @return The response from the request.
     */
    public <TResponse> TResponse sendRequest(IRequest<TResponse> request) {
        return executeInScope(context -> {
            var mediator = context.getBean(Mediator.class);
            return mediator.send(request);
        });
    }

    /**
     * Executes a command using the Command Bus.
     *
     * @param command The command to execute.
     * @param <TResponse> The type of the response.
     * @return The response from the command.
     */
    public <TResponse> TResponse sendCommand(ICommand<TResponse> command) {
        return executeInScope(context -> {
            var commandBus = context.getBean(CommandBus.class);
            return commandBus.send(command);
        });
    }

    /**
     * Executes a command without expecting a response.
     *
     * @param command The command to execute.
     * @param <T> The type of the command.
     */
    public <T extends ICommandUnit> void sendCommand(T command) {
        executeInScope(context -> {
            var commandBus = context.getBean(CommandBus.class);
            commandBus.send(command);
        });
    }

    /**
     * Executes a query using the Query Bus.
     *
     * @param query The query to execute.
     * @param <TResponse> The type of the response.
     * @return The response from the query.
     */
    public <TResponse> TResponse sendQuery(IQuery<TResponse> query) {
        return executeInScope(context -> {
            var queryProcessor = context.getBean(QueryBus.class);
            return queryProcessor.send(query);
        });
    }

    /**
     * Publishes a message using the external event bus.
     *
     * @param message The message to publish.
     * @param <TMessage> The type of the message.
     */
    public <TMessage extends IMessage> void publishMessage(TMessage message) {
        executeInScope(context -> {
            var bus = context.getBean(ExternalEventBus.class);
            bus.publish(message);
        });
    }

    /**
     * Publishes a message wrapped in an event envelope using the external event bus.
     *
     * @param eventEnvelope The event envelope containing the message.
     * @param <TMessage> The type of the message.
     */
    public <TMessage extends IMessage> void publishMessage(IMessageEnvelope<TMessage> eventEnvelope) {
        executeInScope(context -> {
            var bus = context.getBean(ExternalEventBus.class);
            bus.publish(eventEnvelope);
        });
    }
}
