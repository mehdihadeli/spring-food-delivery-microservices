package com.github.mehdihadeli.buildingblocks.abstractions.core.messaging;

import com.github.mehdihadeli.buildingblocks.mediator.abstractions.notifications.INotification;
import java.time.LocalDateTime;
import java.util.UUID;

public interface IMessage extends INotification {
    UUID messageId();

    LocalDateTime created();
}
