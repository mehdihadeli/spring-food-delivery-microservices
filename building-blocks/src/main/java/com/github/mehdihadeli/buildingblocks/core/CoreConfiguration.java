package com.github.mehdihadeli.buildingblocks.core;

import com.github.mehdihadeli.buildingblocks.abstractions.AbstractionRoot;
import com.github.mehdihadeli.buildingblocks.abstractions.core.bean.BeanScopeExecutor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.*;
import com.github.mehdihadeli.buildingblocks.abstractions.core.id.IdGenerator;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceRepository;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.AsyncCommandBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.CommandBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.request.QueryBus;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.MessageSerializer;
import com.github.mehdihadeli.buildingblocks.abstractions.core.serialization.Serializer;
import com.github.mehdihadeli.buildingblocks.core.bean.BeanScopeExecutorImpl;
import com.github.mehdihadeli.buildingblocks.core.data.AuditorAwareUUID;
import com.github.mehdihadeli.buildingblocks.core.events.*;
import com.github.mehdihadeli.buildingblocks.core.id.UlIdIdGenerator;
import com.github.mehdihadeli.buildingblocks.core.messaging.MessageMetadataAccessorImpl;
import com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence.InMemoryMessagePersistenceRepository;
import com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence.MessagePersistenceBackgroundService;
import com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence.MessagePersistenceProperties;
import com.github.mehdihadeli.buildingblocks.core.messaging.messagepersistence.MessagePersistenceServiceImpl;
import com.github.mehdihadeli.buildingblocks.core.request.AsyncCommandBusImpl;
import com.github.mehdihadeli.buildingblocks.core.request.CommandBusImpl;
import com.github.mehdihadeli.buildingblocks.core.request.QueryBusImpl;
import com.github.mehdihadeli.buildingblocks.core.serialization.JacksonMessageSerializerImpl;
import com.github.mehdihadeli.buildingblocks.core.serialization.JacksonSerializerImpl;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.Mediator;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableConfigurationProperties(MessagePersistenceProperties.class)
// for scanning `PersistMessage` entity which is in abstraction package
@AutoConfigurationPackage(
        // `jpa auto-configuration` use `basePackages` which is application root package and all submodules as default
        // for finding repositories and entities. `VerticalSliceTemplateRoot` for finding repositories and interfaces
        basePackageClasses = {CoreRoot.class, AbstractionRoot.class})
public class CoreConfiguration {
    @Bean
    @ConditionalOnMissingBean
    PropertiesService propertiesService(ConfigurableEnvironment environment) {
        return new PropertiesService(environment);
    }

    @Bean
    @ConditionalOnMissingBean
    IdGenerator idGenerator() {
        return new UlIdIdGenerator();
    }

    @Bean("auditorAwareUUID")
    @ConditionalOnMissingBean
    AuditorAware<UUID> auditorAwareUUID() {
        return new AuditorAwareUUID();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "message-persistence",
            name = "message-persist-provider-type",
            havingValue = "inmemory",
            matchIfMissing = true)
    public MessagePersistenceRepository inMemoryMessagePersistenceRepository() {
        return new InMemoryMessagePersistenceRepository();
    }

    @Bean
    @Scope("prototype")
    DomainEventPublisher domainEventPublisher(
            ApplicationContext applicationContext,
            MessageMetadataAccessor messageMetadataAccessor,
            MessagePersistenceService messagePersistenceService,
            InternalEventBus internalEventBus,
            DomainNotificationEventPublisher domainNotificationEventPublisher) {
        return new DomainEventPublisherImpl(
                applicationContext,
                internalEventBus,
                messagePersistenceService,
                domainNotificationEventPublisher,
                messageMetadataAccessor);
    }

    @Bean
    @ConditionalOnMissingBean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    AggregatesDomainEventsRequestStorage aggregatesDomainEventsRequestStorage() {
        return new AggregatesDomainEventsRequestStorageImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    DomainEventsAccessor domainEventsAccessor(AggregatesDomainEventsRequestStorage domainEventsRequestStorage) {
        return new DomainEventAccessorImpl(domainEventsRequestStorage);
    }

    @Bean
    @ConditionalOnMissingBean
    Logger defaultLogger() {
        return LoggerFactory.getLogger("default-logger");
    }

    @Bean
    @ConditionalOnMissingBean
    @Scope("prototype")
    DomainNotificationEventPublisher domainNotificationEventPublisher(
            MessagePersistenceService messagePersistenceService) {
        return new DomainNotificationEventPublisherImpl(messagePersistenceService);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    InternalEventBus internalEventBus(Mediator mediator) {
        return new InternalEventBusImpl(mediator);
    }

    @Bean
    @ConditionalOnMissingBean
    Serializer jacksonSerializer() {
        return new JacksonSerializerImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    MessageSerializer jacksonMessageSerializer() {
        return new JacksonMessageSerializerImpl();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @ConditionalOnMissingBean
    MessageMetadataAccessor messageMetadataAccessor() {
        return new MessageMetadataAccessorImpl();
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    @ConditionalOnMissingBean
    // need to provide `MessagePersistenceRepository` implementation by `PostgresMessagePersistenceRepositoryImpl`
    // provider or other providers
    @ConditionalOnBean({MessagePersistenceRepository.class})
    MessagePersistenceService messagePersistenceService(
            MessagePersistenceRepository messagePersistenceRepository,
            MessageSerializer messageSerializer,
            Mediator mediator,
            // BusDirectPublisher busDirectPublisher,
            ApplicationContext applicationContext,
            Serializer serializer) {
        return new MessagePersistenceServiceImpl(
                messagePersistenceRepository, messageSerializer, mediator, applicationContext, serializer);
    }

    @Bean
    @ConditionalOnMissingBean
    MessagePersistenceBackgroundService messagePersistenceServiceBackgroundService(
            MessagePersistenceProperties messagePersistenceProperties, BeanScopeExecutor beanScopeExecutor) {
        return new MessagePersistenceBackgroundService(beanScopeExecutor, messagePersistenceProperties);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    CommandBus commandBus(
            Mediator mediator,
            MessagePersistenceService messagePersistenceService,
            MessageMetadataAccessor messageMetadataAccessor) {
        return new CommandBusImpl(mediator, messagePersistenceService, messageMetadataAccessor);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    QueryBus queryBus(Mediator mediator) {
        return new QueryBusImpl(mediator);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    AsyncCommandBus asyncCommandBus(
            MessagePersistenceService messagePersistenceService, MessageMetadataAccessor messageMetadataAccessor) {
        return new AsyncCommandBusImpl(messagePersistenceService, messageMetadataAccessor);
    }

    @Bean
    @ConditionalOnMissingBean
    BeanScopeExecutor beanScopeExecutor(ApplicationContext applicationContext) {
        return new BeanScopeExecutorImpl(applicationContext);
    }
}
