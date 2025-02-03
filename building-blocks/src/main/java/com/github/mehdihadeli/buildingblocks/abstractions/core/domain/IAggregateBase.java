package com.github.mehdihadeli.buildingblocks.abstractions.core.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.events.IDomainEvent;
import com.github.mehdihadeli.buildingblocks.core.exceptions.DomainException;
import java.util.List;

public interface IAggregateBase {
    void addDomainEvent(IDomainEvent domainEvent);

    void addDomainEvents(List<IDomainEvent> domainEvent);

    boolean hasUncommittedDomainEvents();

    List<IDomainEvent> dequeueUncommittedDomainEvents();

    List<IDomainEvent> getUncommittedDomainEvents();

    void clearDomainEvents();

    void checkRule(IBusinessRule rule) throws DomainException;
}
