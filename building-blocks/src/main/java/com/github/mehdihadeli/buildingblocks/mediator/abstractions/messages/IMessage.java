package com.github.mehdihadeli.buildingblocks.mediator.abstractions.messages;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IMessage {
    UUID messageId();

    LocalDateTime created();
}
