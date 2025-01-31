package com.github.mehdihadeli.buildingblocks.mediator.abstractions;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotification;

public interface IPublisher {
    <TNotification extends INotification> void publish(TNotification notification) throws RuntimeException;

    void publish(Object notification) throws RuntimeException;
}
