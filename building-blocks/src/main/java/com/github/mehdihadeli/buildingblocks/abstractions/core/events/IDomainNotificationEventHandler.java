package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.events.IEventHandler;

public interface IDomainNotificationEventHandler<TEvent extends IDomainNotificationEvent>
        extends IEventHandler<TEvent> {}
