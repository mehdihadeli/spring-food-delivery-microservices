package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainNotificationEventPublisher;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.messagepersistence.MessagePersistenceService;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import java.util.List;

public class DomainNotificationEventPublisherImpl implements DomainNotificationEventPublisher {
    private final MessagePersistenceService messagePersistenceService;

    public DomainNotificationEventPublisherImpl(MessagePersistenceService messagePersistenceService) {
        this.messagePersistenceService = messagePersistenceService;
    }

    @Override
    public <T extends IDomainEvent> void PublishAsync(IDomainNotificationEvent<T> domainNotificationEvent) {
        ValidationUtils.notBeNull(domainNotificationEvent, "domainNotificationEvent");
        messagePersistenceService.addNotification(domainNotificationEvent);
    }

    @Override
    public <T extends IDomainEvent> void PublishAsync(List<IDomainNotificationEvent<T>> domainNotificationEvents) {
        ValidationUtils.notBeNull(domainNotificationEvents, "domainNotificationEvents");
        for (IDomainNotificationEvent<T> domainNotificationEvent : domainNotificationEvents) {
            PublishAsync(domainNotificationEvent);
        }
    }
}
