package com.github.mehdihadeli.buildingblocks.core.messaging;

import com.github.f4b6a3.ulid.UlidCreator;
import java.util.UUID;

public class MessageUtils {
    private MessageUtils() {}

    public static UUID generateMessageId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateCorrelationId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateNotificationId() {
        return UlidCreator.getUlid().toUuid();
    }

    public static UUID generateInternalId() {
        return UlidCreator.getUlid().toUuid();
    }
}
