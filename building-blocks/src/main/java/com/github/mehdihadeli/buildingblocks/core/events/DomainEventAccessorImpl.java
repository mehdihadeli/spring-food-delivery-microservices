package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.AggregatesDomainEventsRequestStorage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.DomainEventsAccessor;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;

import java.util.List;

public class DomainEventAccessorImpl implements DomainEventsAccessor {
    private final AggregatesDomainEventsRequestStorage aggregatesDomainEventsStorage;

    public DomainEventAccessorImpl(AggregatesDomainEventsRequestStorage aggregatesDomainEventsStorage) {
        this.aggregatesDomainEventsStorage = aggregatesDomainEventsStorage;
    }

    @Override
    public List<IDomainEvent> dequeueUncommittedDomainEvents() {
        return aggregatesDomainEventsStorage.dequeueUncommittedDomainEvents();
    }
}
