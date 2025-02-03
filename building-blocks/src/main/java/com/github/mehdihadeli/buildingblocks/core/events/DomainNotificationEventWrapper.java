package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainNotificationEvent;
import java.util.UUID;

public record DomainNotificationEventWrapper<TDomainEventType extends IDomainEvent>(
        TDomainEventType domainEvent, UUID notificationId) implements IDomainNotificationEvent<TDomainEventType> {}
