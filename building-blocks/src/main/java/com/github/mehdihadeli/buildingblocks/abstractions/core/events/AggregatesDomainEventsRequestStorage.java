package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IAggregateBase;

import java.util.List;

public interface AggregatesDomainEventsRequestStorage {
    /**
     * add events to the store and remove uncommitted events from the aggregate
     * @param aggregate input aggregate
     * @return list ot stored events
     * @param <TAggregate> aggregate type
     */
    <TAggregate extends IAggregateBase> List<IDomainEvent> addEventsFromAggregate(TAggregate aggregate);

    void addEvents(List<IDomainEvent> events);

    List<IDomainEvent> getAllUncommittedEvents();

    List<IDomainEvent> dequeueUncommittedDomainEvents();

    void clearDomainEvents();
}
