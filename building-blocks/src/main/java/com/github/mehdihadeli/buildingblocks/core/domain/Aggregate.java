package com.github.mehdihadeli.buildingblocks.core.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IAggregate;
import com.github.mehdihadeli.buildingblocks.abstractions.core.domain.IBusinessRule;
import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.core.exceptions.BusinessRuleValidationException;
import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Aggregate<TId> extends EntityBase<TId> implements IAggregate<TId> {
    private final ConcurrentLinkedQueue<IDomainEvent> uncommittedDomainEvents = new ConcurrentLinkedQueue<>();

    // Add the domain event to the aggregate's pending changes
    @Override
    public void addDomainEvent(IDomainEvent domainEvent) {
        boolean eventExists = uncommittedDomainEvents.stream().anyMatch(x -> x.equals(domainEvent));

        if (!eventExists) {
            uncommittedDomainEvents.offer(domainEvent);
        }
    }

    @Override
    public void addDomainEvents(List<IDomainEvent> domainEvent) {
        for (IDomainEvent e : domainEvent) {
            addDomainEvent(e);
        }
    }

    @Override
    public boolean hasUncommittedDomainEvents() {
        return !uncommittedDomainEvents.isEmpty();
    }

    @Override
    public List<IDomainEvent> getUncommittedDomainEvents() {
        return List.copyOf(uncommittedDomainEvents);
    }

    @Override
    public void clearDomainEvents() {
        uncommittedDomainEvents.clear();
    }

    @Override
    public List<IDomainEvent> dequeueUncommittedDomainEvents() {
        // create a copy because after clearing events we lost our collection
        var events = List.copyOf(uncommittedDomainEvents);
        clearDomainEvents();

        return events;
    }

    @Override
    public void checkRule(IBusinessRule rule) throws DomainException {
        boolean isBroken = rule.isBroken();
        if (isBroken) {
            throw new BusinessRuleValidationException(rule);
        }
    }
}
