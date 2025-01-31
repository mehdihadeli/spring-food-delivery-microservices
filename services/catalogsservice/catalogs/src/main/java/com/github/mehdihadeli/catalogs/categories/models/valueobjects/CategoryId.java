package com.github.mehdihadeli.catalogs.categories.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;

import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

public record CategoryId(UUID id) implements IdentityId<UUID> {
    public CategoryId {
        notBeEmpty(id, "id");
    }
}
