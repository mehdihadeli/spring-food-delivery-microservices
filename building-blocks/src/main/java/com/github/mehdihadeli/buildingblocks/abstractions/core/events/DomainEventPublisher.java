package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import java.util.List;

public interface DomainEventPublisher {
    void Publish(IDomainEvent domainEvent);

    void Publish(List<IDomainEvent> domainEvents);
}