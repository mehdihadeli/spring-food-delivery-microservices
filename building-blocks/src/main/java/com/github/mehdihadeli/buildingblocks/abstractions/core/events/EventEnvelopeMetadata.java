package com.github.mehdihadeli.buildingblocks.abstractions.core.events;

import com.github.mehdihadeli.buildingblocks.core.utils.DateTimeUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record EventEnvelopeMetadata(
        UUID messageId,
        UUID correlationId,
        String messageType,
        String name,
        UUID causationId,
        Map<String, Object> headers,
        LocalDateTime created,
        long createdUnixTime) {
    public EventEnvelopeMetadata(
            UUID messageId, UUID correlationId, String messageType, String name, UUID causationId) {
        this(
                messageId,
                correlationId,
                messageType,
                name,
                causationId,
                new HashMap<>(), // default empty headers
                LocalDateTime.now(),
                DateTimeUtils.getCurrentEpochSecond());
    }

    public EventEnvelopeMetadata(
            UUID messageId,
            UUID correlationId,
            String messageType,
            String name,
            UUID causationId,
            Map<String, Object> headers) {
        this(
                messageId,
                correlationId,
                messageType,
                name,
                causationId,
                headers,
                LocalDateTime.now(),
                DateTimeUtils.getCurrentEpochSecond());
    }
}
