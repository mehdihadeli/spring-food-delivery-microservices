package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

// Interface for domain notification event mapping
public interface DomainNotificationEventMapper {
    <T extends IDomainEvent> IDomainNotificationEvent<T> mapToDomainNotificationEvent(T domainEvent);
}
