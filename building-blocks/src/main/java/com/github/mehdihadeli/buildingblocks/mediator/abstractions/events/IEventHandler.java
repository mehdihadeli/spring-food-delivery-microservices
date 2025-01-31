package com.github.mehdihadeli.buildingblocks.mediator.abstractions.events;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotification;
import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotificationHandler;

public interface IEventHandler<TEvent extends INotification> extends INotificationHandler<TEvent> {}
