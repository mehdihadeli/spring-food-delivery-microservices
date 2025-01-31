package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

public interface IDomainNotificationEventT<TDomainEventType extends IDomainEvent> extends IDomainNotificationEvent {
    TDomainEventType domainEvent();
}
