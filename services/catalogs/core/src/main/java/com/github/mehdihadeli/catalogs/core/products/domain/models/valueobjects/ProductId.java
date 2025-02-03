package com.github.mehdihadeli.catalogs.core.products.domain.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import java.util.UUID;

public record ProductId(UUID id) implements IdentityId<UUID> {
    public ProductId {
        notBeEmpty(id, "id");
    }
}
