package com.github.mehdihadeli.buildingblocks.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IAggregateBase;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.AggregatesDomainEventsRequestStorage;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AggregatesDomainEventsRequestStorageImpl implements AggregatesDomainEventsRequestStorage {
    private final List<IDomainEvent> uncommittedDomainEvents = new ArrayList<>();

    @Override
    public <TAggregate extends IAggregateBase> List<IDomainEvent> addEventsFromAggregate(TAggregate aggregate) {
        List<IDomainEvent> events = aggregate.dequeueUncommittedDomainEvents();
        addEvents(events);

        return events;
    }

    @Override
    public void addEvents(List<IDomainEvent> events) {
        if (!events.isEmpty()) {
            uncommittedDomainEvents.addAll(events);
        }
    }

    @Override
    public List<IDomainEvent> getAllUncommittedEvents() {
        return Collections.unmodifiableList(uncommittedDomainEvents);
    }

    @Override
    public List<IDomainEvent> dequeueUncommittedDomainEvents() {
        // create a copy because after clearing events we lost our list
        var events = List.copyOf(uncommittedDomainEvents);
        clearDomainEvents();

        return events;
    }

    @Override
    public void clearDomainEvents() {
        this.uncommittedDomainEvents.clear();
    }
}
