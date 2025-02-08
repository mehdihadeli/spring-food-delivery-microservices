package com.github.mehdihadeli.buildingblocks.rabbitmq;

import com.github.mehdihadeli.buildingblocks.abstractions.core.bean.BeanScopeExecutor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.ExternalEventBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.MessageMetadataAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.BusDirectPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.ReflectionUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.StringUtils;
import com.github.mehdihadeli.buildingblocks.core.utils.TypeMapperUtils;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages.IMessageEnvelopeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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
        template.addAfterReceivePostProcessors();

        // Set ConfirmCallback
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                applicationContext
                        .getBeansOfType(AfterPublishPipeline.class)
                        .forEach((key, afterPublishPipeline) -> afterPublishPipeline.handle(correlationData, ack));
            } else {
                System.out.println("Message not confirmed, cause: " + cause);
            }
        });

        applicationContext
                .getBeansOfType(PrePublishPipeline.class)
                .forEach((key, beforePublishPipeline) ->
                        template.addBeforePublishPostProcessors(beforePublishPipeline::handle));

        applicationContext
                .getBeansOfType(ConsumePipeline.class)
                .forEach((key, consumePipeline) -> template.addAfterReceivePostProcessors(consumePipeline::handle));

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
        var envelopeHandlerMap = applicationContext.getBeansOfType(IMessageEnvelopeHandler.class);

        for (Object envelopeHandler : envelopeHandlerMap.values()) {
            if (envelopeHandler instanceof IMessageEnvelopeHandler) {

                Class<?> messageType = ReflectionUtils.getGenericInterfaceTypeParameter(envelopeHandler.getClass());
                if (messageType == null) continue;
                registerMessageHandlerEndpoint(
                        rabbitListenerEndpointRegistrar, (IMessageEnvelopeHandler<?>) envelopeHandler, messageType);
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
    private void registerMessageHandlerEndpoint(
            RabbitListenerEndpointRegistrar registrar, IMessageEnvelopeHandler<?> handler, Class<?> messageType) {

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
                var inputMessageTypeName = message.getMessageProperties().getType();
                var inputMessageType = TypeMapperUtils.getType(inputMessageTypeName);
                logger.atInfo()
                        .addKeyValue("message", SerializerUtils.serializePretty(message))
                        .log("message {} received", message.getClass().getSimpleName());

                var envelope = MessageUtils.convertMessageToEventEnvelope(
                        message, inputMessageType, applicationContext.getBean(MessageSerializer.class));

                var beanScopeExecutor = applicationContext.getBean(BeanScopeExecutor.class);

                // handling inbox pattern
                beanScopeExecutor.executeInScope(scopedContext -> {
                    // Get a new instance of the service for each execution
                    MessagePersistenceService scopedService = scopedContext.getBean(MessagePersistenceService.class);
                    scopedService.addReceivedMessage(envelope);
                });
            } catch (Exception e) {
                // Add proper error handling here
                throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
            }
        });

        registrar.registerEndpoint(endpoint);
    }

    public static Set<String> getAllPackages() {
        Set<String> packageNames = new HashSet<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources("");

            while (resources.hasMoreElements()) {
                File directory = new File(resources.nextElement().toURI());
                findPackagesInDirectory(directory, "", packageNames);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return packageNames;
    }

    private static void findPackagesInDirectory(File directory, String currentPackage, Set<String> packageNames) {
        if (!directory.exists()) return;

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                String newPackage = currentPackage.isEmpty() ? file.getName() : currentPackage + "." + file.getName();
                packageNames.add(newPackage);
                findPackagesInDirectory(file, newPackage, packageNames);
            }
        }
    }
    //
    //    /**
    //     * Helper method to find all classes with a given name (without requiring the package name).
    //     *
    //     * @param className The name of the class to find (e.g., "ProductCreated").
    //     * @return A list of Class objects that match the given name.
    //     */
    //    public static List<Class<?>> findClassesByName(String className) {
    //        List<Class<?>> matchingClasses = new ArrayList<>();
    //
    //        // Create a Reflections object to scan the classpath
    //        Reflections reflections = new Reflections("", Scanners.SubTypes);
    //
    //        // Get all classes in the classpath
    //        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
    //
    //        // Search for classes with the matching name
    //        for (Class<?> clazz : allClasses) {
    //            if (clazz.getSimpleName().equals(className)) {
    //                matchingClasses.add(clazz);
    //            }
    //        }
    //
    //        return matchingClasses;
    //    }

    private String createExchangeForMessageType(Class<?> messageType, RabbitAdmin rabbitAdmin) {

        String exchangeName = MessageUtils.getExchangeName(messageType);
        TopicExchange exchange = new TopicExchange(exchangeName, true, false);
        rabbitAdmin.declareExchange(exchange);
        return exchangeName;
    }

    private String createQueueForMessageType(Class<?> messageType, RabbitAdmin rabbitAdmin) {

        String queueName = MessageUtils.getQueueName(messageType);
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

    private String getHandlerIdentifier(IMessageEnvelopeHandler<?> handler, Class<?> messageType) {

        return "%s-%s-listener"
                .formatted(
                        StringUtils.toKebabCase(handler.getClass().getSimpleName()),
                        StringUtils.toKebabCase(messageType.getSimpleName()));
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
