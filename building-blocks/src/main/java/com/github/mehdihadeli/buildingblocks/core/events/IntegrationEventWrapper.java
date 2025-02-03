package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public record IntegrationEventWrapper<TDomainEventType extends IDomainEvent>(
        TDomainEventType domainEvent, UUID messageId, LocalDateTime created) implements IIntegrationEvent {}
