package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.events.IEvent;

import java.util.UUID;

public interface IDomainNotificationEvent<TDomainEvent extends IDomainEvent> extends IEvent {
    UUID notificationId();

    TDomainEvent domainEvent();
}
