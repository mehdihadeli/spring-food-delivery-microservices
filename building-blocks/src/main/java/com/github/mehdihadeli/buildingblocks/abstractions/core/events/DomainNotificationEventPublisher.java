package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import java.util.List;

public interface DomainNotificationEventPublisher {
    <T extends IDomainEvent> void PublishAsync(IDomainNotificationEvent<T> domainNotificationEvent);

    <T extends IDomainEvent> void PublishAsync(List<IDomainNotificationEvent<T>> domainNotificationEvents);
}
