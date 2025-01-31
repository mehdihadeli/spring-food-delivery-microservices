package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;

// Interface for integration event mapping
public interface IntegrationEventMapper {

    IIntegrationEvent mapToIntegrationEvent(IDomainEvent domainEvent);
}
