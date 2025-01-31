package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;

import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

public record VariantId(UUID id) implements IdentityId<UUID> {
    public VariantId {
        notBeEmpty(id, "id");
    }
}
