package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.*;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.core.utils.SerializerUtils;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomainEventPublisherImpl implements DomainEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(DomainEventPublisherImpl.class);

    private final ApplicationContext applicationContext;
    private final InternalEventBus internalEventBus;
    private final MessagePersistenceService messagePersistenceService;
    private final DomainNotificationEventPublisher domainNotificationEventPublisher;
    private final MessageMetadataAccessor messageMetadataAccessor;

    public DomainEventPublisherImpl(
            ApplicationContext applicationContext,
            InternalEventBus internalEventBus,
            MessagePersistenceService messagePersistenceService,
            DomainNotificationEventPublisher domainNotificationEventPublisher,
            MessageMetadataAccessor messageMetadataAccessor) {
        this.applicationContext = applicationContext;
        this.internalEventBus = internalEventBus;
        this.messagePersistenceService = messagePersistenceService;
        this.domainNotificationEventPublisher = domainNotificationEventPublisher;
        this.messageMetadataAccessor = messageMetadataAccessor;
    }

    @Override
    public void Publish(IDomainEvent domainEvent) {
        ValidationUtils.notBeNull(domainEvent, "domainEvent");

        try {
            logger.atInfo()
                    .addKeyValue("domainEvent", SerializerUtils.serialize(domainEvent))
                    .log(String.format(
                            "Dispatched domain event %s with payload %s",
                            domainEvent.getClass().getSimpleName(), domainEvent));

            // Dispatch events to internal broker
            internalEventBus.publish(domainEvent);

            // Save wrapped integration and notification events to outbox for further processing after commit
            var wrappedNotificationEvent = EventsUtils.getWrappedDomainNotificationEvent(domainEvent);
            if (wrappedNotificationEvent != null) {
                domainNotificationEventPublisher.PublishAsync(wrappedNotificationEvent);
            }

            var wrappedIntegrationEvent = EventsUtils.getWrappedIntegrationEvent(domainEvent);
            if (wrappedIntegrationEvent != null) {
                var correlationId = messageMetadataAccessor.correlationId();
                var cautionId = messageMetadataAccessor.cautionId();
                var integrationEventEnvelope =
                        EventEnvelopeFactory.from(wrappedIntegrationEvent, correlationId, cautionId, new HashMap<>());
                // Save event mapper events into outbox for further processing after commit
                messagePersistenceService.addPublishMessage(integrationEventEnvelope);
            }

            // Find handlers using @Mapper annotation
            Map<String, Object> mappers = applicationContext.getBeansWithAnnotation(Mapper.class);
            var integrationEvents = getIntegrationEvents(domainEvent, mappers);
            if (!integrationEvents.isEmpty()) {
                for (var integrationEvent : integrationEvents) {
                    var correlationId = messageMetadataAccessor.correlationId();
                    var cautionId = messageMetadataAccessor.cautionId();
                    var integrationEventEnvelope =
                            EventEnvelopeFactory.from(integrationEvent, correlationId, cautionId, new HashMap<>());
                    // Save event mapper events into outbox for further processing after commit
                    messagePersistenceService.addPublishMessage(integrationEventEnvelope);
                }
            }

            var domainNotificationEvents = getDomainNotificationEvents(domainEvent, mappers);
            if (!domainNotificationEvents.isEmpty()) {
                for (var domainNotificationEvent : domainNotificationEvents) {
                    // Save event mapper events into outbox for further processing after commit
                    messagePersistenceService.addNotification(domainNotificationEvent);
                }
            }

        } catch (Exception e) {
            logger.atError()
                    .addKeyValue("domainEvent", SerializerUtils.serialize(domainEvent))
                    .log("Error dispatching domain event: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void Publish(List<IDomainEvent> domainEvents) {
        ValidationUtils.notBeNull(domainEvents, "domainEvents");

        for (IDomainEvent domainEvent : domainEvents) {
            Publish(domainEvent);
        }
    }

    private static List<IIntegrationEvent> getIntegrationEvents(IDomainEvent domainEvent, Map<String, Object> mappers) {

        List<IIntegrationEvent> integrationEvents = new ArrayList<>();
        for (Object mapper : mappers.values()) {
            if (mapper instanceof EventMapper eventMapper) {
                var integrationEvent = eventMapper.mapToIntegrationEvent(domainEvent);
                if (integrationEvent != null) {
                    integrationEvents.add(integrationEvent);
                }
            }
        }

        return integrationEvents;
    }

    private static <T extends IDomainEvent> List<IDomainNotificationEvent<T>> getDomainNotificationEvents(
            T domainEvent, Map<String, Object> mappers) {

        List<IDomainNotificationEvent<T>> domainNotificationEvents = new ArrayList<>();
        for (Object mapper : mappers.values()) {
            if (mapper instanceof EventMapper eventMapper) {
                var domainNotificationEvent = eventMapper.mapToDomainNotificationEvent(domainEvent);
                if (domainNotificationEvent != null) {
                    domainNotificationEvents.add(domainNotificationEvent);
                }
            }
        }

        return domainNotificationEvents;
    }
}