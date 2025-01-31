package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import java.util.List;

public interface DomainEventsAccessor {
    List<IDomainEvent> dequeueUncommittedDomainEvents();
}
