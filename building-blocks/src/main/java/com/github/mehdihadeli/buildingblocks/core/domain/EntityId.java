package com.github.mehdihadeli.buildingblocks.core.domain;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import com.github.mehdihadeli.buildingblocks.validation.ValidationUtils;

public record EntityId<TID>(TID id) implements IdentityId<TID> {
    // Compact constructor
    public EntityId {
        ValidationUtils.notBeNull(id, "id");
        if ((id instanceof String && ((String) id).isEmpty())) {
            throw new IllegalArgumentException("Id value cannot be null or empty");
        }
    }

    // Factory method
    public static <T> EntityId<T> from(T id) {
        return new EntityId<>(id);
    }
}
