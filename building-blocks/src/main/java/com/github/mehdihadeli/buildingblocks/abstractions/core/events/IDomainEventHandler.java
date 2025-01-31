package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.events.IEventHandler;

public interface IDomainEventHandler<TEvent extends IDomainEvent> extends IEventHandler<TEvent> {}
