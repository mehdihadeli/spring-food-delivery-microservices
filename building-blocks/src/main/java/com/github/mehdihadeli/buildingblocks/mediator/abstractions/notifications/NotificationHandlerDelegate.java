package com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications;

@FunctionalInterface
public interface NotificationHandlerDelegate {
    void handle() throws RuntimeException;
}
