package com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications;

public interface INotificationHandler<TNotification extends INotification> {

    void handle(TNotification notification) throws RuntimeException;
}
