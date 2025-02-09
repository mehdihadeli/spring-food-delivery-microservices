package com.github.mehdihadeli.shared.catalogs.products.events.integration.v1;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;
import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeNull;

import com.github.mehdihadeli.buildingblocks.abstractions.core.messaging.IIntegrationEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductCreatedV1(UUID messageId, UUID productId, UUID categoryId, String name, LocalDateTime created)
        implements IIntegrationEvent {
    public ProductCreatedV1 {
        notBeNull(messageId, "messageId");
        notBeNull(categoryId, "categoryId");
        notBeNull(name, "name");
        notBeEmpty(name, "name");
        notBeNull(created, "created");
    }
}
