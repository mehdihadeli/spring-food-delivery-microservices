package com.github.mehdihadeli.catalogs.products.domain.models.valueobjects;

import static com.github.mehdihadeli.buildingblocks.validation.ValidationUtils.notBeEmpty;

import com.github.mehdihadeli.buildingblocks.abstractions.core.data.IdentityId;
import java.util.UUID;

public record ProductReviewId(UUID id) implements IdentityId<UUID> {
    public ProductReviewId {
        notBeEmpty(id, "id");
    }
}
