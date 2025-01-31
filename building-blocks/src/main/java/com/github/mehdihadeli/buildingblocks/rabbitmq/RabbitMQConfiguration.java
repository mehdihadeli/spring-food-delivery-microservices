package com.github.mehdihadeli.buildingblocks.rabbitmq;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IEventEnvelope;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.BusDirectPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IMessageHandler;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.MessageHandler;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RabbitTemplate.class})
@EnableConfigurationProperties(CustomRabbitMQProperties.class)
// enable @RabbitListener beside of existing implementations of MessageListener
@EnableRabbit
public class RabbitMQConfiguration implements RabbitListenerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfiguration.class);

    private final ApplicationContext applicationContext;

    public RabbitMQConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnBean(MessagePersistenceService.class)
    @ConditionalOnMissingBean
    ExternalEventBus externalEventBus(
            BusDirectPublisher busDirectPublisher,
            MessageMetadataAccessor messageMetadataAccessor,
            MessagePersistenceService messagePersistenceService,
            CustomRabbitMQProperties customRabbitMQProperties) {
        return new SpringRabbitMQBusImpl(
                busDirectPublisher, messageMetadataAccessor, messagePersistenceService, customRabbitMQProperties);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    BusDirectPublisher busDirectPublisher(
            RabbitTemplate rabbitTemplate, MessageSerializer messageSerializer, RabbitAdmin rabbitAdmin) {
        return new RabbitMQDirectPublisher(rabbitTemplate, messageSerializer, rabbitAdmin);
    }

    // - register `CachingConnectionFactory` as bean is better that `ConnectionFactory`, it can also provide dependency
    // injection for `ConnectionFactory` as subtype and type matching.
    // - It provides access to advanced features like publisher confirms and returns explicitly though
    // `CachingConnectionFactory` type without casting. It makes your configuration more explicit and easier to
    // understand.
    @Bean
    public CachingConnectionFactory connectionFactory(RabbitProperties rabbitProperties) {
        // https://docs.spring.io/spring-amqp/reference/amqp/connections.html#choosing-factory
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        //// https://docs.spring.io/spring-amqp/reference/amqp/template.html#template-confirms
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);

        return connectionFactory;
    }

    // https://docs.spring.io/spring-amqp/reference/amqp/receiving-messages/async-annotation-driven/conversion.html
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // https://docs.spring.io/spring-amqp/reference/amqp/template.html
    // - the AmqpTemplate interface defines all the basic operations for sending and receiving messages. We will explore
    // message sending and reception, respectively, in Sending Messages and Receiving Messages.
    @Bean
    public RabbitTemplate rabbitTemplate(
            CachingConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(500);
        backOffPolicy.setMultiplier(10.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
            CachingConnectionFactory connectionFactory, MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // https: //
        // docs.spring.io/spring-amqp/reference/amqp/receiving-messages/async-annotation-driven/conversion.html
        factory.setMessageConverter(messageConverter);

        return factory;
    }

    // https://docs.spring.io/spring-amqp/reference/amqp/template.html#multi-strict
    @Bean
    TaskExecutor exec() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(10);
        return exec;
    }

    // https://docs.spring.io/spring-amqp/reference/amqp/receiving-messages/async-annotation-driven/registration.html
    // https://docs.spring.io/spring-amqp/reference/amqp/receiving-messages/using-container-factories.html
    // https://docs.spring.io/spring-amqp/reference/amqp/receiving-messages/async-consumer.html
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        // Find handlers using @MessageHandler annotation
        Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(MessageHandler.class);

        for (Object handler : handlers.values()) {
            if (handler instanceof IMessageHandler) {
                registerMessageHandlerEndpoint(rabbitListenerEndpointRegistrar, (IMessageHandler<?>) handler);
            }
        }
    }

    /**
     * Approach 2: Using RabbitListenerConfigurer
     * Advantages:
     * - More Spring-native approach
     * - Separate endpoints for each handler
     * - Individual configuration possible
     * - Easier to add/remove handlers
     */
    private void registerMessageHandlerEndpoint(RabbitListenerEndpointRegistrar registrar, IMessageHandler<?> handler) {

        Class<?> messageType = ReflectionUtils.getGenericInterfaceTypeParameter(handler.getClass());
        if (messageType == null) return;

        var rabbitAdmin = applicationContext.getBean(RabbitAdmin.class);

        // Create exchange, queue, and binding for the message type
        String exchangeName = createExchangeForMessageType(messageType, rabbitAdmin);
        String queueName = createQueueForMessageType(messageType, rabbitAdmin);
        createBindingForMessageType(exchangeName, queueName, messageType, rabbitAdmin);

        // Create endpoint for this handler
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId(getHandlerIdentifier(handler, messageType));
        endpoint.setQueueNames(queueName);
        // use `MessageListener` functional interface
        endpoint.setMessageListener(message -> {
            try {
                logger.atInfo()
                        .addKeyValue("message", SerializerUtils.serializePretty(message))
                        .log("message {} received", message.getClass().getSimpleName());

                var envelope = convertMessageToEventEnvelope(message, messageType);
                handler.HandleInternal(envelope);
            } catch (Exception e) {
                // Add proper error handling here
                throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
            }
        });

        registrar.registerEndpoint(endpoint);
    }

    private String createExchangeForMessageType(Class<?> messageType, RabbitAdmin rabbitAdmin) {

        String exchangeName = "%s-exchange".formatted(StringUtils.toKebabCase(messageType.getSimpleName()));
        TopicExchange exchange = new TopicExchange(exchangeName, true, false);
        rabbitAdmin.declareExchange(exchange);
        return exchangeName;
    }

    private String createQueueForMessageType(Class<?> messageType, RabbitAdmin rabbitAdmin) {

        String queueName = "%s-queue".formatted(StringUtils.toKebabCase(messageType.getSimpleName()));
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);
        return queueName;
    }

    private void createBindingForMessageType(
            String exchangeName, String queueName, Class<?> messageType, RabbitAdmin rabbitAdmin) {

        String routingKey = StringUtils.toKebabCase(messageType.getSimpleName());
        org.springframework.amqp.core.Binding binding = new org.springframework.amqp.core.Binding(
                queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);
        rabbitAdmin.declareBinding(binding);
    }

    private String getHandlerIdentifier(IMessageHandler<?> handler, Class<?> messageType) {

        return "%s-%s-listener"
                .formatted(
                        StringUtils.toKebabCase(handler.getClass().getSimpleName()),
                        StringUtils.toKebabCase(messageType.getSimpleName()));
    }

    private <TMessage extends IMessage> IEventEnvelope<TMessage> convertMessageToEventEnvelope(
            Message message, Class<?> messageType) {

        var messageSerializer = applicationContext.getBean(MessageSerializer.class);
        MessageProperties props = message.getMessageProperties();
        Map<String, Object> headers = props.getHeaders();

        // Object messageBody = messageConverter.fromMessage(message);
        // our message body is EventEnvelope
        IEventEnvelope<TMessage> result = messageSerializer.deserialize(new String(message.getBody()), messageType);
        if (result.metadata().headers() != null) {
            result.metadata().headers().putAll(headers);
        }

        return result;
    }

    //    /**
    //     * Approach 1: Using SimpleMessageListenerContainer
    //     * Advantages:
    //     * - Single container for all handlers
    //     * - Better resource utilization
    //     * - Centralized error handling
    //     * - Easier monitoring
    //     * - More control over concurrency
    //     */
    //    @Bean
    //    public SimpleMessageListenerContainer messageListenerContainer(
    //            CachingConnectionFactory connectionFactory, MessageConverter messageConverter) {
    //        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
    //
    //        // Find all handlers marked with @MessageHandler
    //        Map<String, Object> handlers = applicationContext.getBeansWithAnnotation(MessageHandler.class);
    //
    //        List<String> queueNames = new ArrayList<>();
    //        Map<String, MessageListener> queueListeners = new HashMap<>();
    //
    //        // Register each handler
    //        for (Object handler : handlers.values()) {
    //            if (handler instanceof IMessageHandler) {
    //                registerHandlerForContainer((IMessageHandler<?>) handler, queueNames, queueListeners);
    //            }
    //        }
    //
    //        // Configure the container with all queues
    //        container.setQueueNames(queueNames.toArray(new String[0]));
    //
    //        // Single message listener that routes to appropriate handler
    //        container.setMessageListener(message -> {
    //            String queueName = message.getMessageProperties().getConsumerQueue();
    //            MessageListener listener = queueListeners.get(queueName);
    //            if (listener != null) {
    //                listener.onMessage(message);
    //            }
    //        });
    //
    //        // Global container settings
    //        container.setPrefetchCount(1);
    //        container.setConcurrentConsumers(3);
    //        container.setMaxConcurrentConsumers(10);
    //        container.setDefaultRequeueRejected(false);
    //
    //        return container;
    //    }
    //
    //    private void registerHandlerForContainer(
    //            IMessageHandler<?> handler, List<String> queueNames, Map<String, MessageListener> queueListeners) {
    //
    //        Class<?> messageType = getMessageTypeFromHandler(handler.getClass());
    //        if (messageType == null) return;
    //
    //        var rabbitmqAdmin = applicationContext.getBean(RabbitAdmin.class);
    //
    //        // Setup infrastructure
    //        String exchangeName = createExchangeForMessageType(messageType, rabbitmqAdmin);
    //        String queueName = createQueueForMessageType(messageType, rabbitmqAdmin);
    //        createBindingForMessageType(exchangeName, queueName, messageType, rabbitmqAdmin);
    //
    //        queueNames.add(queueName);
    //
    //        // Create dedicated listener for this handler
    //        MessageListener listener = message -> {
    //            try {
    //                var envelope = convertMessageToEventEnvelope(message, messageType);
    //                handler.HandleInternal(envelope);
    //            } catch (Exception e) {
    //                throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
    //            }
    //        };
    //
    //        queueListeners.put(queueName, listener);
    //    }
}
