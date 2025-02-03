package com.github.mehdihadeli.catalogs.core.categories.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import java.util.UUID;

public record CategoryId(UUID id) implements IdentityId<UUID> {
    public CategoryId {
        notBeEmpty(id, "id");
    }
}
