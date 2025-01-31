package com.github.mehdihadeli.buildingblocks.core.domain;

import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;
import java.util.UUID;

public record UUIDEntityId(UUID id) {
    // Compact constructor
    public UUIDEntityId {
        ValidationUtils.notBeNull(id, "id");
        ValidationUtils.notBeEmpty(id, "id");
    }

    // Factory method
    public static UUIDEntityId createEntityId(UUID id) {
        return new UUIDEntityId(id);
    }

    // Convenience method to create with random UUID
    public static UUIDEntityId random() {
        return new UUIDEntityId(UUID.randomUUID());
    }
}
