package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;

import java.util.UUID;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

public record CustomerId(UUID id) implements IdentityId<UUID> {
    public CustomerId {
        notBeEmpty(id, "id");
    }
}
