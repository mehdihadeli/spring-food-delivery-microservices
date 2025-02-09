package com.github.mehdihadeli.buildingblocks.test;

import com.github.mehdihadeli.buildingblocks.abstractions.core.bean.BeanScopeExecutor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.TestHarness;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageDeliveryType;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessageStatus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.IInternalCommand;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
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
import jakarta.persistence.EntityManager;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;
import org.assertj.core.util.TriFunction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;

public class SharedFixture {
    private ApplicationContext applicationContext;
    // TestRestTemplate is not an extension of RestTemplate, but rather an alternative that simplifies integration
    // testing and facilitates authentication during tests. It helps in customization of Apache HTTP client.
    private TestRestTemplate testRestTemplate;
    private BeanScopeExecutor beanScopeExecutor;
    private RabbitTemplate rabbitTemplate;

    private static PostgresTestContainerFixture postgresContainer;
    private static RabbitMQTestContainerFixture rabbitmqContainer;
    private static MongoTestContainerFixture mongoTestContainer;

    public SharedFixture() {
        postgresContainer = new PostgresTestContainerFixture();
        rabbitmqContainer = new RabbitMQTestContainerFixture();
        mongoTestContainer = new MongoTestContainerFixture();

        postgresContainer.startContainer();
        rabbitmqContainer.startContainer();
        mongoTestContainer.startContainer();
        // Initialize WireMock server
        // Start WireMock server on a specific port
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(6060));
        wireMockServer.start();
        WireMock.configureFor("localhost", 6060);
    }

    protected static WireMockServer wireMockServer;

    public void initialize(ApplicationContext applicationContext) {
        // Use RabbitTemplate to intercept and verify the message
        this.rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        this.testRestTemplate = applicationContext.getBean(TestRestTemplate.class);
        this.testRestTemplate.withBasicAuth("test", "test");
        this.beanScopeExecutor = applicationContext.getBean(BeanScopeExecutor.class);
    }

    public void dispose() {
        postgresContainer.stopContainer();
        rabbitmqContainer.stopContainer();
        mongoTestContainer.stopContainer();
        wireMockServer.stop();
    }

    public void resetDatabasesAsync() {
        postgresContainer.resetDatabase();
        mongoTestContainer.resetDatabase();
    }

    public void cleanupMessaging() {
        rabbitmqContainer.cleanupQueues();
    }

    public void configureTestProperties(DynamicPropertyRegistry registry) {
        configureMongoProperties(registry);
        configurePostgresProperties(registry);
        configureRabbitMQProperties(registry);
    }

    private void configurePostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
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

    public void waitUntilConditionMet(Callable<Boolean> conditionToMet) {
        waitUntilConditionMet(conditionToMet, 300, null);
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

    public <TInternalCommand extends IInternalCommand> void shouldProcessingInternalCommand(
            Class<TInternalCommand> internalCommandClass) {
        waitUntilConditionMet(() -> {
            boolean hasElement = executeInScope(context -> {
                var messagePersistenceService = context.getBean(MessagePersistenceService.class);

                var result = messagePersistenceService.getByFilter(
                        MessageStatus.Delivered,
                        MessageDeliveryType.Internal,
                        MessageUtils.getFullTypeName(internalCommandClass));

                return !result.isEmpty();
            });
            return hasElement;
        });
    }

    public <TMessage extends IMessage> void shouldProcessingOutboxMessage(Class<TMessage> messageClass) {
        waitUntilConditionMet(() -> {
            boolean hasElement = executeInScope(context -> {
                var messagePersistenceService = context.getBean(MessagePersistenceService.class);

                var result = messagePersistenceService.getByFilter(
                        MessageStatus.Delivered,
                        MessageDeliveryType.Outbox,
                        MessageUtils.getFullTypeName(messageClass));

                return !result.isEmpty();
            });
            return hasElement;
        });
    }

    /**
     * Waits for a message of the specified type to be published.
     *
     * @param messageType The class of the message to wait for.
     * @param <TMessage>   The type of the message, which must implement IMessage.
     */
    public <TMessage extends IMessage> void shouldPublishing(Class<TMessage> messageType) {
        executeInScope(context -> {
            var testHarness = context.getBean(TestHarness.class);
            testHarness.waitForPublishedMessage(messageType);
        });
    }

    /**
     * Waits for a message of the specified type to be consumed.
     *
     * @param messageType The class of the message to wait for.
     * @param <TMessage>  The type of the message, which must implement IMessage.
     */
    public <TMessage extends IMessage> void shouldConsuming(Class<TMessage> messageType) {
        executeInScope(context -> {
            var testHarness = context.getBean(TestHarness.class);
            testHarness.waitForConsumedMessage(messageType);
        });
    }

    public <T> T executeEntityManager(BiFunction<EntityManager, Mediator, T> action) {
        return executeInScope(context -> {
            return action.apply(context.getBean(EntityManager.class), context.getBean(Mediator.class));
        });
    }

    public void executeEntityManager(BiConsumer<EntityManager, Mediator> action) {
        executeInScope(context -> {
            action.accept(context.getBean(EntityManager.class), context.getBean(Mediator.class));
        });
    }

    public void executeEntityManager(TriConsumer<ApplicationContext, EntityManager, Mediator> action) {
        executeInScope(context -> {
            action.accept(context, context.getBean(EntityManager.class), context.getBean(Mediator.class));
        });
    }

    public <T> T executeEntityManager(TriFunction<ApplicationContext, EntityManager, Mediator, T> action) {
        return executeInScope(context -> {
            return action.apply(context, context.getBean(EntityManager.class), context.getBean(Mediator.class));
        });
    }
}
