package com.github.mehdihadeli.catalogs.products.features.creatingproduct.v1.events.integration;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

public record ProductCreated(UUID messageId, UUID productId, UUID categoryId, String name, LocalDateTime created)
        implements IIntegrationEvent {
    public ProductCreated {
        notBeNull(messageId, "messageId");
        notBeNull(categoryId, "categoryId");
        notBeNull(name, "name");
        notBeEmpty(name, "name");
        notBeNull(created, "created");
    }
}
